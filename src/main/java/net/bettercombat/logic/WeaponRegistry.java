package net.bettercombat.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class WeaponRegistry {
    static final Logger LOGGER = LogUtils.getLogger();
    static Map<Identifier, WeaponAttributes> registrations = new HashMap();

    public static void register(Identifier itemId, WeaponAttributes attributes) {
        registrations.put(itemId, attributes);
    }

    public static WeaponAttributes getAttributes(Identifier itemId) {
        return registrations.get(itemId);
    }

    public static WeaponAttributes getAttributes(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        Item item = itemStack.getItem();
        Identifier id = Registry.ITEM.getId(item);
        WeaponAttributes attributes = WeaponRegistry.getAttributes(id);
        return attributes;
    }

    // LOADING

    public static void loadAttributes(ResourceManager resourceManager) {
        var gson = new Gson();
        Type fileFormat = new TypeToken<AttributesContainer>() {}.getType();
        Map<Identifier, AttributesContainer> containers = new HashMap();
        // Reading all attribute files
        resourceManager.findResources("weapon_attributes", fileName -> fileName.getPath().endsWith(".json")).forEach((identifier, resource) -> {
            try {
                // System.out.println("Checking resource: " + identifier);
                JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream()));
                AttributesContainer container = gson.fromJson(reader, fileFormat);
                var id = identifier
                        .toString().replace("weapon_attributes/", "");
                id = id.substring(0, id.lastIndexOf('.'));
                containers.put(new Identifier(id), container);
            } catch (Exception e) {
                System.err.println("Failed to parse: " + identifier);
                e.printStackTrace();
            }
        });

        // Resolving parents
        containers.forEach( (key, value) -> {
            if (!Registry.ITEM.containsId(key)) {
                return;
            }
            try {
                ArrayList<WeaponAttributes> resolutionChain = new ArrayList();
                AttributesContainer current = value;
                while (current != null) {
                    resolutionChain.add(0, current.attributes());
                    if (current.parent() != null) {
                        current = containers.get(new Identifier(current.parent()));
                    } else {
                        current = null;
                    }
                }

                var empty = new WeaponAttributes(0, null, false, null);
                var resolvedAttributes = resolutionChain
                    .stream()
                    .reduce(empty, (a, b) -> {
                        if (b == null) { // I'm not sure why null can enter as `b`
                            return a;
                        }
                        return WeaponAttributesHelper.override(a, b);
                    });

                WeaponAttributesHelper.validate(resolvedAttributes);
                register(key, resolvedAttributes);
            } catch (Exception e) {
                LOGGER.error("Failed to resolve weapon attributes for: " + key + ". Reason: " + e.getMessage());
            }
        });

        encodeRegistry();
    }

    // NETWORK SYNC

    private static PacketByteBuf encodedRegistrations = PacketByteBufs.create();

    public static void encodeRegistry() {
        PacketByteBuf buffer = PacketByteBufs.create();
        var gson = new Gson();
        var json = gson.toJson(registrations);
        LOGGER.info("Weapon Attribute registry loaded: " + json);

        List<String> chunks = new ArrayList<>();
        var chunkSize = 10000;
        for (int i = 0; i < json.length(); i += chunkSize) {
            chunks.add(json.substring(i, Math.min(json.length(), i + chunkSize)));
        }

        buffer.writeInt(chunks.size());
        for (var chunk: chunks) {
            buffer.writeString(chunk);
        }

        LOGGER.info("Encoded Weapon Attribute registry size (with package overhead): " + buffer.readableBytes()
                + " bytes (in " + chunks.size() + " string chunks with the size of "  + chunkSize + ")");
        encodedRegistrations = buffer;
    }

    public static void decodeRegistry(PacketByteBuf buffer) {
        var chunkCount = buffer.readInt();
        String json = "";
        for (int i = 0; i < chunkCount; ++i) {
            json = json.concat(buffer.readString());
        }
        LOGGER.info("Decoded Weapon Attribute registry in " + chunkCount + " string chunks");
        LOGGER.info("Weapon Attribute registry received: " + json);
        var gson = new Gson();
        Type mapType = new TypeToken<Map<String, WeaponAttributes>>() {}.getType();
        Map<String, WeaponAttributes> readRegistrations = gson.fromJson(json, mapType);
        Map<Identifier, WeaponAttributes> newRegistrations = new HashMap();
        readRegistrations.forEach((key, value) -> {
            newRegistrations.put(new Identifier(key), value);
        });
        registrations = newRegistrations;
    }

    public static PacketByteBuf getEncodedRegistry() {
        return encodedRegistrations;
    }
}
