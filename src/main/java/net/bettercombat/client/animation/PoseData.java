package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

import java.util.UUID;

public record PoseData(UUID uuid, boolean isMirrored) {
    public static PoseData from(KeyframeAnimation animation, boolean isMirrored) {
        UUID uuid = null;
        if (animation != null) {
            uuid = animation.getUuid();
        }
        return new PoseData(uuid, isMirrored);
    }
}
