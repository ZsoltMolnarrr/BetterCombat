package net.bettercombat.client;

import com.mojang.logging.LogUtils;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationRegistry {
    static final Logger LOGGER = LogUtils.getLogger();
    public static Map<String, KeyframeAnimation> emotes = new HashMap<>(); // TODO - Rename

    public static void load(ResourceManager resourceManager) {
        var dataFolder = "weapon_animations";
        for (Identifier identifier : resourceManager.findResources(dataFolder, fileName -> fileName.endsWith(".json"))) {
            try {
                var resource = resourceManager.getResource(identifier);
                List<KeyframeAnimation> animations = AnimationSerializing.deserializeAnimation(resource.getInputStream());
                KeyframeAnimation animation = animations.get(0);

                var id = identifier
                        .toString()
                        .replace(dataFolder + "/", "");
                id = id.substring(0, id.lastIndexOf('.'));
                AnimationRegistry.emotes.put(id, animation);
            } catch (Exception e) {
                LOGGER.error("Failed to load animation " + identifier.toString());
                e.printStackTrace();
            }
        }
    }
}
