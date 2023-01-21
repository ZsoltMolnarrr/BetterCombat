package net.bettercombat.client.animation.first_person;

import net.bettercombat.logic.AnimatedHand;

public class FirstPersonRenderHelper {
    public static boolean isRenderingFirstPersonPlayerModel = false;
//    public static boolean isAttackingWithOffHand = false;


    public static AnimationProperties current = AnimationProperties.defaults;
    public static void resetProperties() {
        current = AnimationProperties.defaults;
    }

    public record AnimationProperties(AnimatedHand hand) {
        public static AnimationProperties defaults = new AnimationProperties(AnimatedHand.MAIN_HAND);
    }
}
