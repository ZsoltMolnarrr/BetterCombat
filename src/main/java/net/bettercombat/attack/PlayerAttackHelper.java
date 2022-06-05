package net.bettercombat.attack;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;

public class PlayerAttackHelper {
    public static float getDualWieldingAttackSpeedMultiplier(PlayerEntity player) {
        return isDualWielding(player) ? 2F : 1F;
    }

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
            var itemStack = isOffHand
                    ? player.getOffHandStack()
                    : player.getMainHandStack();
            var attributes = WeaponRegistry.getAttributes(itemStack);
            int handSpecificComboCount = ( (isOffHand && comboCount > 0) ? (comboCount - 1) : (comboCount) ) / 2;
            return new AttackHand(attributes.currentAttack(handSpecificComboCount), isOffHand, attributes, itemStack);
        } else {
            var itemStack = player.getMainHandStack();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
            if (attributes != null) {
                return new AttackHand(attributes.currentAttack(comboCount), false, attributes, itemStack);
            }
        }
        return null;
    }

    public static void setAttributesForOffHandAttack(PlayerEntity player, boolean useOffHand) {
        var mainHandStack = player.getMainHandStack();
        var offHandStack = player.getOffHandStack();
        ItemStack add;
        ItemStack remove;
        if (useOffHand) {
            remove = mainHandStack;
            add = offHandStack;
        } else {
            remove = offHandStack;
            add = mainHandStack;
        }
        if (remove != null) {
            player.getAttributes().removeModifiers(remove.getAttributeModifiers(MAINHAND));
        }
        if (add != null) {
            player.getAttributes().addTemporaryModifiers(add.getAttributeModifiers(MAINHAND));
        }
    }
}
