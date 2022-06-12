package net.bettercombat.client;

import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import net.bettercombat.api.WeaponAttributes;
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
                // System.out.println("Checking resource: " + identifier);
                var resource = resourceManager.getResource(identifier);
                List<EmoteData> emotes = UniversalEmoteSerializer.readData(resource.getInputStream(), null, "json");
                EmoteData emote = emotes.get(0);
                emote.isBuiltin = true;
                var id = identifier
                        .toString()
                        .replace(dataFolder + "/", "");
                id = id.substring(0, id.lastIndexOf('.'));
                // System.out.println("Registering id: " + id + " animation:" + emote);
                AnimationRegistry.emotes.put(id, emote);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static EmoteData getPose(WeaponAttributes attributes) {
        var namespace = "bettercombat:";
        String animationName = null;
        if (attributes == null) {
            return null;
        }
        switch (attributes.held()) {
            case SWORD_ONE_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case SWORD_TWO_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case AXE_ONE_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case AXE_TWO_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case HAMMER_ONE_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case HAMMER_TWO_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case POLEARM_ONE_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case POLEARM_TWO_HANDED -> {
                animationName = namespace + "2h_sword_pose";
            }
            case STAFF -> {
                animationName = namespace + "2h_sword_pose";
            }
            case DAGGER -> {
                animationName = namespace + "2h_sword_pose";
            }
            case FIST -> {
                animationName = namespace + "2h_sword_pose";
            }
        }
        return emotes.get(animationName);
    }
}
