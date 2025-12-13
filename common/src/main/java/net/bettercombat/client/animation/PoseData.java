package net.bettercombat.client.animation;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PoseData(UUID uuid, boolean isMirrored) {
    public static PoseData from(String animationId, boolean isMirrored) {
        UUID uuid = null;
        if (animationId != null) {
            uuid = UUID.nameUUIDFromBytes(animationId.getBytes(StandardCharsets.UTF_8));
        }
        return new PoseData(uuid, isMirrored);
    }
}
