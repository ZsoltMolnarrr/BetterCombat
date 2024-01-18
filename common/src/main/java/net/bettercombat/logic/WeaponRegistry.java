package net.bettercombat.logic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponRegistry {
    static final Logger LOGGER = LogUtils.getLogger();
    static Map<Identifier, WeaponAttributes> registrations = new HashMap();
    static Map<Identifier, AttributesContainer> containers = new HashMap();

    public static void register(Identifier itemId, WeaponAttributes attributes) {
        registrations.put(itemId, attributes);
    }

    static WeaponAttributes getAttributes(Identifier itemId) {
        return registrations.get(itemId);
    }

    public static WeaponAttributes getAttributes(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack.hasNbt()) {
            var attributes = WeaponAttributesHelper.readFromNBT(itemStack);
            if (attributes != null) {
                return attributes;
            }
        }
        Item item = itemStack.getItem();
        Identifier id = Registries.ITEM.getId(item);
        WeaponAttributes attributes = WeaponRegistry.getAttributes(id);
        return attributes;
    }

    // LOADING

    public static void loadAttributes(ResourceManager resourceManager) {
        loadContainers(resourceManager);

        // Resolving parents
        containers.forEach((itemId, container) -> {
            if (!Registries.ITEM.containsId(itemId)) {
                return;
            }
            resolveAndRegisterAttributes(itemId, container);
        });
    }

    private static void loadContainers(ResourceManager resourceManager) {
        var gson = new Gson();
        Map<Identifier, AttributesContainer> containers = new HashMap();
        // Reading all attribute files
        for (var entry : resourceManager.findResources("weapon_attributes", fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
            var identifier = entry.getKey();
            var resource = entry.getValue();
            try {
                // System.out.println("Checking resource: " + identifier);
                JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream()));
                AttributesContainer container = WeaponAttributesHelper.decode(reader);
                var id = identifier
                        .toString().replace("weapon_attributes/", "");
                id = id.substring(0, id.lastIndexOf('.'));
                containers.put(new Identifier(id), container);
            } catch (Exception e) {
                System.err.println("Failed to parse: " + identifier);
                e.printStackTrace();
            }
        }
        WeaponRegistry.containers = containers;
    }

    public static WeaponAttributes resolveAttributes(Identifier itemId, AttributesContainer container) {
        try {
            ArrayList<WeaponAttributes> resolutionChain = new ArrayList();
            AttributesContainer current = container;
            while (current != null) {
                resolutionChain.add(0, current.attributes());
                if (current.parent() != null) {
                    current = containers.get(new Identifier(current.parent()));
                } else {
                    current = null;
                }
            }

            var empty = new WeaponAttributes(0, null, null, false, null, null);
            var resolvedAttributes = resolutionChain
                    .stream()
                    .reduce(empty, (a, b) -> {
                        if (b == null) { // I'm not sure why null can enter as `b`
                            return a;
                        }
                        return WeaponAttributesHelper.override(a, b);
                    });

            WeaponAttributesHelper.validate(resolvedAttributes);
            return resolvedAttributes;
        } catch (Exception e) {
            LOGGER.error("Failed to resolve weapon attributes for: " + itemId + ". Reason: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void resolveAndRegisterAttributes(Identifier itemId, AttributesContainer container) {
        var resolvedAttributes = resolveAttributes(itemId, container);
        if (resolvedAttributes != null) {
            register(itemId, resolvedAttributes);
        }
    }

    // NETWORK SYNC

    private static PacketByteBuf encodedRegistrations = PacketByteBufs.create();

    public static void encodeRegistry() {
        PacketByteBuf buffer = PacketByteBufs.create();
        var gson = new Gson();

        Map<String, JsonElement> data = new HashMap<>();
        data.put("weapon_attributes", gson.toJsonTree(registrations));
        data.put("attribute_containers", gson.toJsonTree(containers));

        var json = gson.toJson(data);

        if (BetterCombat.config.weapon_registry_logging) {
            LOGGER.info("Encoded registries: " + json);
        }

        List<String> chunks = new ArrayList<>();
        var chunkSize = 10000;
        for (int i = 0; i < json.length(); i += chunkSize) {
            chunks.add(json.substring(i, Math.min(json.length(), i + chunkSize)));
        }

        buffer.writeInt(chunks.size());

        for (var chunk : chunks) {
            buffer.writeString(chunk);
        }

        LOGGER.info("Encoded registries size (with package overhead): " + buffer.readableBytes()
                + " bytes (in " + chunks.size() + " string chunks with the size of " + chunkSize + ")");
        encodedRegistrations = buffer;
    }

    public static void decodeRegistry(PacketByteBuf buffer) {
        var totalChunkCount = buffer.readInt();

        StringBuilder jsonBuilder = new StringBuilder();

        for (int i = 0; i < totalChunkCount; ++i) {
            jsonBuilder.append(buffer.readString());
            LOGGER.info("Decoded registry in chunk " + (i + 1));
        }

        String json = jsonBuilder.toString();

        if (BetterCombat.config.weapon_registry_logging) {
            LOGGER.info("Decoded registries: " + json);
        }

        var gson = new Gson();
        Type mapType = new TypeToken<Map<String, JsonElement>>() {
        }.getType();
        Type weaponAttributeType = new TypeToken<Map<String, WeaponAttributes>>() {
        }.getType();
        Type attributeContainerType = new TypeToken<Map<String, AttributesContainer>>() {
        }.getType();
        Map<String, JsonElement> data = gson.fromJson(json, mapType);

        Map<String, WeaponAttributes> readRegistrations = gson.fromJson(data.get("weapon_attributes"), weaponAttributeType);
        Map<Identifier, WeaponAttributes> newRegistrations = new HashMap();
        readRegistrations.forEach((key, value) -> {
            newRegistrations.put(new Identifier(key), value);
        });

        Map<String, AttributesContainer> readAttributes = gson.fromJson(data.get("attribute_containers"), attributeContainerType);
        Map<Identifier, AttributesContainer> newAttributes = new HashMap();
        readAttributes.forEach((key, value) -> {
            newAttributes.put(new Identifier(key), value);
        });

        registrations = newRegistrations;
        containers = newAttributes;
    }

    public static PacketByteBuf getEncodedRegistry() {
        return encodedRegistrations;
    }
}
