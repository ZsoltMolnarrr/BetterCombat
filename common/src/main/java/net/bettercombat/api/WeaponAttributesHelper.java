package net.bettercombat.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InvalidObjectException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class WeaponAttributesHelper {
    public static WeaponAttributes override(WeaponAttributes a, WeaponAttributes b) {
        var attackRange = b.attackRange() > 0 ? b.attackRange() : a.attackRange();
        var rangeBonus = b.rangeBonus() != 0 ? b.rangeBonus() : a.rangeBonus();
        var pose = b.pose() != null ? b.pose() : a.pose();
        var off_hand_pose = b.offHandPose() != null ? b.offHandPose() : a.offHandPose();
        var isTwoHanded = b.two_handed() != null ? b.two_handed() : a.two_handed();
        var category = b.category() != null ? b.category() : a.category();
        var attacks = a.attacks();
        if (b.attacks() != null && b.attacks().length > 0) {
            var overrideAttacks = new ArrayList<WeaponAttributes.Attack>();
            for(int i = 0; i < b.attacks().length; ++i) {
                var base = (a.attacks() != null && a.attacks().length > i)
                        ? a.attacks()[i]
                        : WeaponAttributes.Attack.empty();
                var override = b.attacks()[i];
                var attack = new WeaponAttributes.Attack(
                        override.conditions() != null ? override.conditions() : base.conditions(),
                        override.hitbox() != null ? override.hitbox() : base.hitbox(),
                        override.damageMultiplier() != 0 ? override.damageMultiplier() : base.damageMultiplier(),
                        override.movementSpeedMultiplier() != 0 ? override.movementSpeedMultiplier() : base.movementSpeedMultiplier(),
                        override.rangeMultiplier() != 0 ? override.rangeMultiplier() : base.rangeMultiplier(),
                        override.angle() != 0 ? override.angle() : base.angle(),
                        override.upswing() != 0 ? override.upswing() : base.upswing(),
                        override.animation() != null ? override.animation() : base.animation(),
                        override.swingSound() != null ? override.swingSound() : base.swingSound(),
                        override.impactSound() != null ? override.impactSound() : base.impactSound(),
                        (override.trailParticles() != null && !override.trailParticles().isEmpty()) ? override.trailParticles() : base.trailParticles());
                overrideAttacks.add(attack);
            }
            attacks = overrideAttacks.toArray(new WeaponAttributes.Attack[0]);
        }
        var trailAppearance = b.trailAppearance() != null ? b.trailAppearance() : a.trailAppearance();
        return new WeaponAttributes(attackRange, rangeBonus, pose, off_hand_pose, isTwoHanded, category, attacks, trailAppearance);
    }

    public static void validate(WeaponAttributes attributes) throws Exception {
        if (attributes.attacks() == null) {
            return;
        }
        if (attributes.attacks().length > 0) {
            var index = 0;
            for (WeaponAttributes.Attack attack : attributes.attacks()) {
                try {
                    validate(attack);
                } catch(InvalidObjectException exception) {
                    var message = "Invalid attack at index:" + index + " - " + exception.getMessage();
                    throw new InvalidObjectException(message);
                }
                index += 1;
            }
        }
    }

    private static void validate(WeaponAttributes.Attack attack) throws InvalidObjectException {
        if (attack.hitbox() == null) {
            throw new InvalidObjectException("Undefined `hitbox`");
        }
        if (attack.damageMultiplier() < 0) {
            throw new InvalidObjectException("Invalid `damage_multiplier`");
        }
        if (attack.angle() < 0) {
            throw new InvalidObjectException("Invalid `angle`");
        }
        if (attack.upswing() < 0) {
            throw new InvalidObjectException("Invalid `upswing`");
        }
        if (attack.animation() == null || attack.animation().length() == 0) {
            throw new InvalidObjectException("Undefined `animation`");
        }
    }

    private static Type attributesContainerFileFormat = new TypeToken<AttributesContainer>() {}.getType();

    public static AttributesContainer decode(Reader reader) {
        var gson = new Gson();
        AttributesContainer container = gson.fromJson(reader, attributesContainerFileFormat);
        return container;
    }

    public static AttributesContainer decode(JsonReader json) {
        var gson = new Gson();
        AttributesContainer container = gson.fromJson(json, attributesContainerFileFormat);
        return container;
    }

    public static String encode(AttributesContainer container) {
        var gson = new Gson();
        return gson.toJson(container);
    }
}
