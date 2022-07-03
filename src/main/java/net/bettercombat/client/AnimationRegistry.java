package net.bettercombat.client;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationRegistry { // TODO - Call at client init
    public static Map<String, KeyframeAnimation> emotes = new HashMap<>(); // TODO - Rename

    public static void load(ResourceManager resourceManager) {
        var dataFolder = "weapon_animations";
        for (Identifier identifier : resourceManager.findResources(dataFolder, fileName -> fileName.endsWith(".json"))) {
            try {
                var resource = resourceManager.getResource(identifier);
                List<KeyframeAnimation> animations = AnimationSerializing.deserializeAnimation(resource.getInputStream());
                KeyframeAnimation animation = emotes.get(0);
                animation.isBuiltin = true;
                // animation.bodyParts.get("head").pitch.isEnabled = false;

                var id = identifier
                        .toString()
                        .replace(dataFolder + "/", "");
                id = id.substring(0, id.lastIndexOf('.'));
                AnimationRegistry.emotes.put(id, animation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
