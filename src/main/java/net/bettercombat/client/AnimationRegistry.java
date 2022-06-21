package net.bettercombat.client;

import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationRegistry {
    public static Map<String, EmoteData> emotes = new HashMap<>();

    public static void load(ResourceManager resourceManager) {
        var dataFolder = "weapon_animations";
        for (Identifier identifier : resourceManager.findResources(dataFolder, fileName -> fileName.endsWith(".json"))) {
            try {
                var resource = resourceManager.getResource(identifier);
                List<EmoteData> emotes = UniversalEmoteSerializer.readData(resource.getInputStream(), null, "json");
                EmoteData emote = emotes.get(0);
                emote.isBuiltin = true;
                emote.bodyParts.get("head").pitch.isEnabled = false;

                var id = identifier
                        .toString()
                        .replace(dataFolder + "/", "");
                id = id.substring(0, id.lastIndexOf('.'));
                AnimationRegistry.emotes.put(id, emote);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
