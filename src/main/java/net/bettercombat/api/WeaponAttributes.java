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
        private final HitBoxShape hitbox;
        private final double damageMultiplier;
        private final double angle;
        private final double upswing;
        private final String animation;
        private final Sound[] swingSound;
        private final Sound[] impactSound;

        public Attack(
                HitBoxShape hitbox,
                double damageMultiplier,
                double angle,
                double upswing,
                String animation,
                Sound[] swingSound,
                Sound[] impactSound
        ) {
            this.hitbox = hitbox;
            this.damageMultiplier = damageMultiplier;
            this.angle = angle;
            this.upswing = upswing;
            this.animation = animation;
            this.swingSound = swingSound;
            this.impactSound = impactSound;
        }

        public HitBoxShape hitbox() {
            return hitbox;
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

        public Sound[] swingSound() {
            return swingSound;
        }

        public Sound[] impactSound() {
            return impactSound;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Attack) obj;
            return Objects.equals(this.hitbox, that.hitbox) &&
                    Double.doubleToLongBits(this.damageMultiplier) == Double.doubleToLongBits(that.damageMultiplier) &&
                    Double.doubleToLongBits(this.angle) == Double.doubleToLongBits(that.angle) &&
                    Double.doubleToLongBits(this.upswing) == Double.doubleToLongBits(that.upswing) &&
                    Objects.equals(this.animation, that.animation) &&
                    Objects.equals(this.swingSound, that.swingSound) &&
                    Objects.equals(this.impactSound, that.impactSound);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hitbox, damageMultiplier, angle, upswing, animation, swingSound, impactSound);
        }

        @Override
        public String toString() {
            return "Attack[" +
                    "hitbox=" + hitbox + ", " +
                    "damageMultiplier=" + damageMultiplier + ", " +
                    "angle=" + angle + ", " +
                    "upswing=" + upswing + ", " +
                    "animation=" + animation + ", " +
                    "swingSound=" + swingSound + ", " +
                    "impactSound=" + impactSound + ']';
        }
    }

    public enum HitBoxShape {
        FORWARD_BOX,
        VERTICAL_PLANE,
        HORIZONTAL_PLANE
    }

    public static final class Sound {
        private final String id;
        private final float volume;
        private final float pitch;
        private final float randomness;

        public Sound(String id, float volume, float pitch, float randomness) {
            this.id = id;
            this.volume = volume;
            this.pitch = pitch;
            this.randomness = randomness;
        }

        public String id() {
            return id;
        }

        public float volume() {
            return volume;
        }

        public float pitch() {
            return pitch;
        }

        public float randomness() {
            return randomness;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Sound) obj;
            return Objects.equals(this.id, that.id) &&
                    Float.floatToIntBits(this.volume) == Float.floatToIntBits(that.volume) &&
                    Float.floatToIntBits(this.pitch) == Float.floatToIntBits(that.pitch) &&
                    Float.floatToIntBits(this.randomness) == Float.floatToIntBits(that.randomness);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, volume, pitch, randomness);
        }

        @Override
        public String toString() {
            return "SoundV2[" +
                    "id=" + id + ", " +
                    "volume=" + volume + ", " +
                    "pitch=" + pitch + ", " +
                    "randomness=" + randomness + ']';
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
