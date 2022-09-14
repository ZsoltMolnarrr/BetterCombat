package net.bettercombat.api;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents how a weapon behaves when player performs attack with it.
 */
public final class WeaponAttributes {

    /**
     * The maximal range of any attacks made with this weapon.
     */
    private final double attack_range;

    /**
     * The pose animation to play when idling.
     * Applied when weapon is in the main hand.
     * Value must be an identifier, formula: "namespace:resource".
     * Suggested to be only used for two-handed weapons.
     *
     * Example values:
     *   "bettercombat:sword-pose"
     *   "my-mod-id:my-sword-pose"
     */
    @Nullable
    private final String pose;

    /**
     * The pose animation to play when idling.
     * Applied when weapon is in the off hand.
     * Value must be an identifier, formula: "namespace:resource".
     * Suggested to be only used for two-handed weapons.
     *
     * Example values:
     *   "bettercombat:sword-pose"
     *   "my-mod-id:my-sword-pose"
     */
    @Nullable
    private final String off_hand_pose;

    /**
     * Determines whether the weapon counts as two-handed or one-handed weapon.
     * When the player's selected item is two-handed weapon, the off-hand slot is completely disabled.
     * When the player's selected item is one-handed weapon, another one-handed weapon can be placed into
     */
    private final Boolean two_handed;

    /**
     * Specifies the category (aka family) type of the weapon.
     * This can be any value. Prefer using lowercase values.
     *
     * Example values:
     *    "cutlass"
     *    "sickle"
     *
     * Used for conditional combos.
     */
    @Nullable
    private final String category;

    /**
     * Specifies the sequence of attacks following each other, when the user is attacking continuously.
     * (When last attack of the sequence is reached, it restarts)
     * With a sequence of different attacks, you can create combos.
     * Check out the member wise documentation of `Attack` (in this file), to see how they can be different.
     *
     * When using attribute inheritance, the inherited sequence of attacks can be reduced or extended.
     * Example reducing (inherited attributes have a sequence of 3 attack):
     *   "attacks": [ {}, {} ]
     * Example of extending  (inherited attributes have a sequence of 2 attack):
     *   "attacks": [ {}, {}, { ... my new fully parsable attack object ... } ]
     * The properties of inherited attack objects can be overridden.
     */
    private final Attack[] attacks;

    public WeaponAttributes(
            double attack_range,
            @Nullable String pose,
            @Nullable String off_hand_pose,
            Boolean isTwoHanded,
            String category,
            Attack[] attacks) {
        this.attack_range = attack_range;
        this.pose = pose;
        this.off_hand_pose = off_hand_pose;
        this.two_handed = isTwoHanded;
        this.category = category;
        this.attacks = attacks;
    }

    /**
     * Represents a single weapon swing.
     */
    public static final class Attack {
        /**
         * Conditions those need to be fulfilled for the attack to be performed,
         * otherwise the attack is skipped.
         * For no conditions use `null` or empty array.
         */
        private Condition[] conditions;

        /**
         * Determines the shape of the attack hitbox.
         * (This shape is scaled by the `attack_range` property from the embedding object.)
         * For accepted values check out `HitBoxShape` (in this file).
         */
        private HitBoxShape hitbox;

        /**
         * Applies damage multiplier to a single attack in the sequence.
         * (So different attacks in a combo can do more or less damage compared to each other.)
         * Example values:
         *   for +30% damage, use the value `1.3`
         *   for -30% damage, use the value `0.7`
         */
        private double damage_multiplier = 1;

        /**
         * Determines the angle (measured in degrees) of the attack's hitbox, centered to the player's look vector.
         * Targeted entities outside of this 3D slice are not hit.
         * If set to `0`, no angle check is done.
         * Example values:
         *   for an attack with an angle of 90 degrees, use the value `90`
         *   (targeted entities where position vector compared to player's look vector is bigger than 45, will not be hit)
         */
        private double angle = 0;

        /**
         * Determines the amount of time, after the attack is performed during the attack animation.
         * The actual amount of time is relative to the cooldown of the weapon.
         * Example values:
         *   to perform an attack at 60% of the attack cooldown, use the value `0.6`
         *   (in case of a sword with 1.6 speed (12 ticks attack cooldown), the attack will be performed
         *   after 7.2 (rounded to 7) ticks of upswing)
         *
         * Try to align the tipping point of the animation and the time at which the attack is performed
         * as close as possible.
         * Formula to calculate attack cooldown in ticks: (1 / (4 - ATTACK_SPEED)) * 20
         */
        private double upswing = 0;

        /**
         * The attack animation to play.
         * Value must be an identifier, formula: "namespace:resource".
         * Example values:
         *   "bettercombat:sword-slash"
         *   "my-mod-id:my-sword-swing"
         */
        private String animation = null;

        /**
         * The sound to play upon executing the attack.
         * Check out the member wise documentation of `Sound` (in this file).
         */
        private Sound swing_sound = null;

        /**
         * Not working at the moment.
         */
        private Sound impact_sound = null;

        /**
         * This empty initializer is needed for GSON, to support parsing over default values
         */
        public Attack() { }

        public Attack(
                Condition[] conditions,
                HitBoxShape hitbox,
                double damage_multiplier,
                double angle,
                double upswing,
                String animation,
                Sound swing_sound,
                Sound impact_sound
        ) {
            this.conditions = conditions;
            this.hitbox = hitbox;
            this.damage_multiplier = damage_multiplier;
            this.angle = angle;
            this.upswing = upswing;
            this.animation = animation;
            this.swing_sound = swing_sound;
            this.impact_sound = impact_sound;
        }

        @Nullable
        public Condition[] conditions() {
            return conditions;
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

        public Sound swingSound() {
            return swing_sound;
        }

        public Sound impactSound() {
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

    public enum Condition {
        /**
         * Fulfilled if the player is not dual wielding weapons
         */
        NOT_DUAL_WIELDING,
        /**
         * Fulfilled if the player is dual wielding any weapons
         */
        DUAL_WIELDING_ANY,
        /**
         * Fulfilled if the player is dual wielding items with matching ids
         */
        DUAL_WIELDING_SAME,
        /**
         * Fulfilled if the player is dual wielding items with matching categories
         * (Category of an item is specified at `WeaponAttributes.category`)
         */
        DUAL_WIELDING_SAME_CATEGORY,
        /**
         * Fulfilled if the player has not item at all in the off-hand
         */
        NO_OFFHAND_ITEM,
        /**
         * Fulfilled if the player has a shield in the off-hand
         */
        OFF_HAND_SHIELD,
        /**
         * Fulfilled for attacks performed with main-hand only
         */
        MAIN_HAND_ONLY,
        /**
         * Fulfilled for attacks performed with off-hand only
         */
        OFF_HAND_ONLY
    }

    /**
     * Represents a sound to be played with arguments.
     */
    public static final class Sound {
        /**
         * The id of the sound.
         * Must be specified with a non-null and non-empty value!
         * If using your own sounds, make sure you register in your mod initializer.
         *
         * Value must be an identifier, formula: "namespace:resource".
         * Example values:
         *   "bettercombat:sword-swing"
         *   "my-mod-id:my-sword-sound"
         */
        private String id = null;

        /**
         * Volume of the sound
         * Has default value, optional to specify.
         */
        private float volume = 1;

        /**
         * Pitch of the sound
         * Has default value, optional to specify.
         */
        private float pitch = 1;


        /**
         * This empty initializer is needed for GSON, to support parsing over default values
         */
        public Sound() { }

        public Sound(String id) {
            this.id = id;
        }

        /**
         * Pitch randomness of the sound.
         * Has default value, optional to specify.
         * Example values:
         *   for additional pitch within a range of +/- 10%, use the value `0.1`
         */
        private float randomness = 0.1F;

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

    public double attackRange() {
        return attack_range;
    }

    @Nullable
    public String pose() {
        return pose;
    }

    @Nullable
    public String offHandPose() {
        return off_hand_pose;
    }

    @Nullable
    public String category() {
        return category;
    }

    public boolean isTwoHanded() {
        return two_handed != null ? two_handed.booleanValue() : false;
    }

    public Boolean two_handed() {
        return two_handed;
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
                Objects.equals(this.pose, that.pose) &&
                Objects.equals(this.two_handed, that.two_handed) &&
                Objects.equals(this.attacks, that.attacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack_range, two_handed, attacks);
    }

    @Override
    public String toString() {
        return "WeaponAttributes[" +
                "attack_range=" + attack_range + ", " +
                "pose=" + pose + ", " +
                "isTwoHanded=" + two_handed + ", " +
                "attacks=" + attacks + ']';
    }

}
