package net.bettercombat.attack;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerAttackHelper {
    public static boolean shouldAttackWithOffHand(PlayerEntity player, int comboCount) {
        return PlayerAttackHelper.isDualWielding(player) && comboCount % 2 == 1;
    }

    public static boolean isDualWielding(PlayerEntity player) {
        var mainAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        var offAttributes = WeaponRegistry.getAttributes(player.getOffHandStack());
        return mainAttributes != null && !mainAttributes.held().isTwoHanded()
                && offAttributes != null && !offAttributes.held().isTwoHanded();
    }

    public static AttackHand getCurrentAttack(PlayerEntity player, int comboCount) {
        if (isDualWielding(player)) {
            boolean isOffHand = shouldAttackWithOffHand(player,comboCount);
            var attributes = isOffHand
                    ? WeaponRegistry.getAttributes(player.getOffHandStack())
                    : WeaponRegistry.getAttributes(player.getMainHandStack());
            int handSpecificComboCount = ( (isOffHand && comboCount > 0) ? (comboCount - 1) : (comboCount) ) / 2;
            return new AttackHand(attributes.currentAttack(handSpecificComboCount), isOffHand, attributes);
        } else {
            WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
            if (attributes != null) {
                return new AttackHand(attributes.currentAttack(comboCount), false, attributes);
            }
        }
        return null;
    }
}
