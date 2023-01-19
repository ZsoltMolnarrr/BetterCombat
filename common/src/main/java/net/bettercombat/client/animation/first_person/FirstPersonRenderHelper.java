package net.bettercombat.client.animation.first_person;

public class FirstPersonRenderHelper {
    public static boolean isRenderingFirstPersonPlayerModel = false;
    public static boolean isAttackingWithOffHand = false;

    public static AnimationProperties current = AnimationProperties.defaults;

    public record AnimationProperties(boolean isOffhand, boolean isTwoHanded) {
        public static AnimationProperties defaults = new AnimationProperties(false, false);
    }
}
