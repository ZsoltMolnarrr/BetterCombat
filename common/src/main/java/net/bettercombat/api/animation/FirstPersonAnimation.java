package net.bettercombat.api.animation;

import net.bettercombat.client.animation.FirstPersonRenderHelper;

public class FirstPersonAnimation {
    public static boolean isRenderingAttackAnimationInFirstPerson() {
        return FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel;
    }
}
