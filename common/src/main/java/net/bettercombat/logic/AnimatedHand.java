package net.bettercombat.logic;

public enum AnimatedHand {
    MAIN_HAND, OFF_HAND, TWO_HANDED;

    public static AnimatedHand from(boolean isOffHand, boolean isTwoHanded) {
        if (isTwoHanded) {
            return TWO_HANDED;
        }
        if (isOffHand) {
            return OFF_HAND;
        }
        return MAIN_HAND;
    }

    public boolean isOffHand() {
        return this == OFF_HAND;
    }
}
