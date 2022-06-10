package net.bettercombat.api;

import java.util.Objects;

public final class WeaponAttributes {
    private final double attack_range;
    private final Held held;
    private final Attack[] attacks;

    public WeaponAttributes(
            double attack_range,
            Held held,
            Attack[] attacks) {
        this.attack_range = attack_range;
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
        private final double damage_multiplier;
        private final double angle;
        private final double upswing;
        private final String animation;
        private final Sound[] swing_sound;
        private final Sound[] impact_sound;

        public Attack(
                HitBoxShape hitbox,
                double damage_multiplier,
                double angle,
                double upswing,
                String animation,
                Sound[] swing_sound,
                Sound[] impact_sound
        ) {
            this.hitbox = hitbox;
            this.damage_multiplier = damage_multiplier;
            this.angle = angle;
            this.upswing = upswing;
            this.animation = animation;
            this.swing_sound = swing_sound;
            this.impact_sound = impact_sound;
        }

        public HitBoxShape hitbox() {
            return hitbox;
        }

        public double damageMultiplier() {
            return damage_multiplier;
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
            return swing_sound;
        }

        public Sound[] impactSound() {
            return impact_sound;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Attack) obj;
            return Objects.equals(this.hitbox, that.hitbox) &&
                    Double.doubleToLongBits(this.damage_multiplier) == Double.doubleToLongBits(that.damage_multiplier) &&
                    Double.doubleToLongBits(this.angle) == Double.doubleToLongBits(that.angle) &&
                    Double.doubleToLongBits(this.upswing) == Double.doubleToLongBits(that.upswing) &&
                    Objects.equals(this.animation, that.animation) &&
                    Objects.equals(this.swing_sound, that.swing_sound) &&
                    Objects.equals(this.impact_sound, that.impact_sound);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hitbox, damage_multiplier, angle, upswing, animation, swing_sound, impact_sound);
        }

        @Override
        public String toString() {
            return "Attack[" +
                    "hitbox=" + hitbox + ", " +
                    "damage_multiplier=" + damage_multiplier + ", " +
                    "angle=" + angle + ", " +
                    "upswing=" + upswing + ", " +
                    "animation=" + animation + ", " +
                    "swing_sound=" + swing_sound + ", " +
                    "impact_sound=" + impact_sound + ']';
        }
    }

    public enum HitBoxShape {
        FORWARD_BOX,
        VERTICAL_PLANE,
        HORIZONTAL_PLANE
    }

    public static final class Sound {
        private final String id = null;
        private final float volume = 1;
        private final float pitch = 1;
        private final float randomness = 0.1F;

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
        return attack_range;
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
        return Double.doubleToLongBits(this.attack_range) == Double.doubleToLongBits(that.attack_range) &&
                Objects.equals(this.held, that.held) &&
                Objects.equals(this.attacks, that.attacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack_range, held, attacks);
    }

    @Override
    public String toString() {
        return "WeaponAttributes[" +
                "attack_range=" + attack_range + ", " +
                "held=" + held + ", " +
                "attacks=" + attacks + ']';
    }

}
