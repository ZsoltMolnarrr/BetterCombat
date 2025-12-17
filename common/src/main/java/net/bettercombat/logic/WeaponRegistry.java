package net.bettercombat.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.bettercombat.api.component.BetterCombatDataComponents;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.CompressionHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponRegistry {
    static final Logger LOGGER = LogUtils.getLogger();
    // Actual attributes to weapon assignments
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
//        var attributes = WeaponAttributesHelper.readFromNBT(itemStack);
//        if (attributes != null) {
//            return attributes;
//        }

        var component = itemStack.get(BetterCombatDataComponents.WEAPON_PRESET_ID);
        if (component != null) {
            var container = containers.get(component);
            if (container != null) {
                return container.attributes();
            }
        }
        Item item = itemStack.getItem();
        Identifier id = Registries.ITEM.getId(item);
        return WeaponRegistry.getAttributes(id);
    }

    // LOADING

    public static void loadAttributes(ResourceManager resourceManager) {
        loadContainers(resourceManager);

        // Resolving parents
        containers.forEach( (itemId, container) -> {
            if (!Registries.ITEM.containsId(itemId)) {
                return;
            }
            resolveAndRegisterAttributes(itemId, container);
        });
    }

    private static void loadContainers(ResourceManager resourceManager) {
        Map<Identifier, AttributesContainer> containers = new HashMap();
        var logging = BetterCombatMod.config.weapon_registry_logging;
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
                containers.put(Identifier.of(id), container);
                if (logging) {
                    System.out.println("Loaded container: " + id);
                }
            } catch (Exception e) {
                System.err.println("Failed to parse: " + identifier);
                e.printStackTrace();
            }
        }

        // Do not remove this
        WeaponRegistry.containers = containers;
        // The following container resolution will use these containers

        Map<Identifier, AttributesContainer> resolvedContainers = new HashMap();
        for (var entry : containers.entrySet()) {
            var id = entry.getKey();
            var container = entry.getValue();
            if (container.parent() != null) {
                var resolvedAttributes = resolveAttributes(id, container);
                if (resolvedAttributes != null) {
                    container = new AttributesContainer(null, resolvedAttributes);
                }
            }
            resolvedContainers.put(id, container);
        }

        WeaponRegistry.containers = resolvedContainers;
    }

    public static WeaponAttributes resolveAttributes(Identifier itemId, AttributesContainer container) {
        try {
            ArrayList<WeaponAttributes> resolutionChain = new ArrayList();
            AttributesContainer current = container;
            while (current != null) {
                resolutionChain.add(0, current.attributes());
                if (current.parent() != null) {
                    current = containers.get(Identifier.of(current.parent()));
                } else {
                    current = null;
                }
            }

            var empty = WeaponAttributes.empty();
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

    private static Encoded encodedRegistrations = new Encoded(true, List.of());
    public record Encoded(boolean compressed, List<String> chunks) {}
    private static final int CHUNK_SIZE = 10000;
    private static final Gson gson = new GsonBuilder().create();
    public static class SyncFormat {
        public Map<String, AttributesContainer> attributes = new HashMap<>();
        public Map<String, WeaponAttributes> registrations = new HashMap<>();
    }

    public static void encodeRegistry() {
        var compressed = BetterCombatMod.config.weapon_registry_compression;
        List<String> chunks = new ArrayList<>();
        var syncContent = new SyncFormat();
        containers.forEach((key, value) -> {
            syncContent.attributes.put(key.toString(), value);
        });
        registrations.forEach((key, value) -> {
            syncContent.registrations.put(key.toString(), value);
        });

        var json = gson.toJson(syncContent);
        if (compressed) {
            json = CompressionHelper.gzipCompress(json);
        }
        if (BetterCombatMod.config.weapon_registry_logging) {
            LOGGER.info("Weapon Attribute assignments loaded: " + json);
        }
        for (int i = 0; i < json.length(); i += CHUNK_SIZE) {
            chunks.add(json.substring(i, Math.min(json.length(), i + CHUNK_SIZE)));
        }

        encodedRegistrations = new Encoded(compressed, chunks);

        var referencePacket = new Packets.WeaponRegistrySync(compressed, chunks);
        var buffer = Platform.createByteBuffer();
        referencePacket.write(buffer);
        LOGGER.info("Encoded Weapon Attribute registry size (with package overhead): " + buffer.readableBytes()
                + " bytes (in " + chunks.size() + " string chunks with the size of "  + CHUNK_SIZE + ")");
    }

    public static void decodeRegistry(Packets.WeaponRegistrySync syncPacket) {
        var compressed = syncPacket.compressed();
        String json = "";
        for (var chunk : syncPacket.chunks()) {
            json = json.concat(chunk);
        }
        if (compressed) {
            json = CompressionHelper.gzipDecompress(json);
        }
        LOGGER.info("Decoded Weapon Attribute registry in " + syncPacket.chunks().size() + " string chunks");
        if (BetterCombatMod.config.weapon_registry_logging) {
            LOGGER.info("Weapon Attribute registry received: " + json);
        }

        SyncFormat sync = gson.fromJson(json, SyncFormat.class);
        containers.clear();
        sync.attributes.forEach((key, value) -> {
            containers.put(Identifier.of(key), value);
        });
        registrations.clear();
        sync.registrations.forEach((key, value) -> {
            registrations.put(Identifier.of(key), value);
        });
    }

    public static Encoded getEncodedRegistry() {
        return encodedRegistrations;
    }
}
