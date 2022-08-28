package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

public class StateCollectionHelper {
    public static void configure(KeyframeAnimation.StateCollection bodyPart, boolean isRotationEnabled, boolean isOffsetEnabled) {
        bodyPart.pitch.setEnabled(isRotationEnabled);
        bodyPart.roll.setEnabled(isRotationEnabled);
        bodyPart.yaw.setEnabled(isRotationEnabled);
        bodyPart.x.setEnabled(isOffsetEnabled);
        bodyPart.y.setEnabled(isOffsetEnabled);
        bodyPart.z.setEnabled(isOffsetEnabled);
    }
}
