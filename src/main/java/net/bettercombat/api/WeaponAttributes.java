package net.bettercombat.api;

public record WeaponAttributes(
        double attackRange,
        net.bettercombat.api.WeaponAttributes.Held held,
        net.bettercombat.api.WeaponAttributes.Attack[] attacks) {

    public enum Held {
        SWORD_ONE_HANDED,
        SWORD_TWO_HANDED,
        AXE_ONE_HANDED,
        AXE_TWO_HANDED,
        HAMMER_ONE_HANDED,
        HAMMER_TWO_HANDED,
        POLEARM_ONE_HANDED,
        POLEARM_TWO_HANDED,
        STAFF,
        DAGGER,
        FIST;

        public boolean isTwoHanded() {
            switch (this) {
                case SWORD_TWO_HANDED,
                        AXE_TWO_HANDED,
                        HAMMER_TWO_HANDED,
                        POLEARM_TWO_HANDED,
                        STAFF -> {
                    return true;
                }
            }
            return false;
        }
    }

    public record Attack(
            SwingDirection direction,
            double damageMultiplier,
            double angle,
            Sound swingSound,
            Sound impactSound
    ) {
    }

    public enum SwingDirection {
        FORWARD,
        VERTICAL_TOP_TO_BOTTOM,
        HORIZONTAL_RIGHT_TO_LEFT,
        HORIZONTAL_LEFT_TO_RIGHT,
    }

    public record Sound(
            String id,
            float pitchRandomness
    ) {
    }

    // Helpers

    public WeaponAttributes.Attack currentAttack(int comboCount) {
        int index = comboCount % this.attacks().length;
        return this.attacks()[index];
    }
}
