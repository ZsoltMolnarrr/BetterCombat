package net.bettercombat.client.animation.first_person;

import net.bettercombat.logic.AnimatedHand;

public class FirstPersonRenderHelper {
    public static AnimationProperties current = AnimationProperties.defaults;
    public static void resetProperties() {
        current = AnimationProperties.defaults;
    }

    public record AnimationProperties(AnimatedHand hand) {
        public static AnimationProperties defaults = new AnimationProperties(AnimatedHand.MAIN_HAND);
    }

    private static FirstPersonAnimation renderCycleData;
    public static boolean isRenderCycleFirstPerson() {
        return renderCycleData != null;
    }

    public static FirstPersonAnimation getRenderCycleData() {
        return renderCycleData;
    }

    public static void setFirstPersonRenderCycle(FirstPersonAnimation animation) {
        renderCycleData = animation;
    }

    public static void clearFirstPersonRenderCycle() {
        renderCycleData = null;
    }
}
