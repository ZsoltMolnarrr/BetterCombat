package net.bettercombat.api;

import java.util.Objects;

public final class WeaponAttributes {
    private final double attackRange;
    private final Held held;
    private final Attack[] attacks;

    public WeaponAttributes(
            double attackRange,
            Held held,
            Attack[] attacks) {
        this.attackRange = attackRange;
        this.held = held;
        this.attacks = attacks;
    }

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

    public static final class Attack {
        private final SwingDirection direction;
        private final double damageMultiplier;
        private final double angle;
        private final double upswing;
        private final String animation;
        private final Sound swingSound;
        private final Sound impactSound;

        public Attack(
                SwingDirection direction,
                double damageMultiplier,
                double angle,
                double upswing,
                String animation,
                Sound swingSound,
                Sound impactSound
        ) {
            this.direction = direction;
            this.damageMultiplier = damageMultiplier;
            this.angle = angle;
            this.upswing = upswing;
            this.animation = animation;
            this.swingSound = swingSound;
            this.impactSound = impactSound;
        }

        public SwingDirection direction() {
            return direction;
        }

        public double damageMultiplier() {
            return damageMultiplier;
        }

        public double angle() {
            return angle;
        }

        public double upswing() {
            return upswing;
        }

        public String animation() {
            return animation;
        }

        public Sound swingSound() {
            return swingSound;
        }

        public Sound impactSound() {
            return impactSound;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Attack) obj;
            return Objects.equals(this.direction, that.direction) &&
                    Double.doubleToLongBits(this.damageMultiplier) == Double.doubleToLongBits(that.damageMultiplier) &&
                    Double.doubleToLongBits(this.angle) == Double.doubleToLongBits(that.angle) &&
                    Double.doubleToLongBits(this.upswing) == Double.doubleToLongBits(that.upswing) &&
                    Objects.equals(this.animation, that.animation) &&
                    Objects.equals(this.swingSound, that.swingSound) &&
                    Objects.equals(this.impactSound, that.impactSound);
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, damageMultiplier, angle, upswing, animation, swingSound, impactSound);
        }

        @Override
        public String toString() {
            return "Attack[" +
                    "direction=" + direction + ", " +
                    "damageMultiplier=" + damageMultiplier + ", " +
                    "angle=" + angle + ", " +
                    "upswing=" + upswing + ", " +
                    "animation=" + animation + ", " +
                    "swingSound=" + swingSound + ", " +
                    "impactSound=" + impactSound + ']';
        }
    }

    public enum SwingDirection {
        FORWARD,
        VERTICAL_TOP_TO_BOTTOM,
        HORIZONTAL_RIGHT_TO_LEFT,
        HORIZONTAL_LEFT_TO_RIGHT,
    }

    public static final class Sound {
        private final String id;
        private final float pitchRandomness;

        public Sound(
                String id,
                float pitchRandomness
        ) {
            this.id = id;
            this.pitchRandomness = pitchRandomness;
        }

        public String id() {
            return id;
        }

        public float pitchRandomness() {
            return pitchRandomness;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Sound) obj;
            return Objects.equals(this.id, that.id) &&
                    Float.floatToIntBits(this.pitchRandomness) == Float.floatToIntBits(that.pitchRandomness);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, pitchRandomness);
        }

        @Override
        public String toString() {
            return "Sound[" +
                    "id=" + id + ", " +
                    "pitchRandomness=" + pitchRandomness + ']';
        }

    }

    // Helpers

    public Attack currentAttack(int comboCount) {
        int index = comboCount % this.attacks().length;
        return this.attacks()[index];
    }

    public double attackRange() {
        return attackRange;
    }

    public Held held() {
        return held;
    }

    public Attack[] attacks() {
        return attacks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WeaponAttributes) obj;
        return Double.doubleToLongBits(this.attackRange) == Double.doubleToLongBits(that.attackRange) &&
                Objects.equals(this.held, that.held) &&
                Objects.equals(this.attacks, that.attacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attackRange, held, attacks);
    }

    @Override
    public String toString() {
        return "WeaponAttributes[" +
                "attackRange=" + attackRange + ", " +
                "held=" + held + ", " +
                "attacks=" + attacks + ']';
    }

}
