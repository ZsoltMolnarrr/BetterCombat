package net.bettercombat.logic;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;

public class PlayerAttackHelper {
    public static float getDualWieldingAttackSpeedMultiplier(PlayerEntity player) {
        return isDualWielding(player) ? BetterCombat.config.dual_wielding_attack_speed_multiplier : 1F;
    }

    public static float getDualWieldingAttackDamageMultiplier(PlayerEntity player, AttackHand hand) {
        return isDualWielding(player)
                ? (hand.isOffHand()
                    ? BetterCombat.config.dual_wielding_off_hand_damage_multiplier
                    : BetterCombat.config.dual_wielding_main_hand_damage_multiplier)
                : 1;
    }

    public static boolean shouldAttackWithOffHand(PlayerEntity player, int comboCount) {
        return PlayerAttackHelper.isDualWielding(player) && comboCount % 2 == 1;
    }

    public static boolean isDualWielding(PlayerEntity player) {
        var mainAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        var offAttributes = WeaponRegistry.getAttributes(player.getOffHandStack());
        return mainAttributes != null && !mainAttributes.isTwoHanded()
                && offAttributes != null && !offAttributes.isTwoHanded();
    }

    public static AttackHand getCurrentAttack(PlayerEntity player, int comboCount) {
        if (isDualWielding(player)) {
            boolean isOffHand = shouldAttackWithOffHand(player,comboCount);
            var itemStack = isOffHand
                    ? player.getOffHandStack()
                    : player.getMainHandStack();
            var attributes = WeaponRegistry.getAttributes(itemStack);
            int handSpecificComboCount = ( (isOffHand && comboCount > 0) ? (comboCount - 1) : (comboCount) ) / 2;
            var attack = selectAttack(handSpecificComboCount, attributes, player, isOffHand);
            return new AttackHand(attack, isOffHand, attributes, itemStack);
        } else {
            var itemStack = player.getMainHandStack();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
            if (attributes != null) {
                var attack = selectAttack(comboCount, attributes, player, false);
                return new AttackHand(attack, false, attributes, itemStack);
            }
        }
        return null;
    }

    private static WeaponAttributes.Attack selectAttack(int comboCount, WeaponAttributes attributes, PlayerEntity player, boolean isOffHandAttack) {
        var attacks = attributes.attacks();
        attacks = Arrays.stream(attacks)
                .filter(attack ->
                        attack.conditions() == null
                        || attack.conditions().length == 0
                        || evaluateConditions(attack.conditions(), player, isOffHandAttack)
                )
                .toArray(WeaponAttributes.Attack[]::new);
        int index = comboCount % attacks.length;
        return attacks[index];
    }

    private static boolean evaluateConditions(WeaponAttributes.Condition[] conditions, PlayerEntity player, boolean isOffHandAttack) {
        return Arrays.stream(conditions).allMatch(condition -> evaluateCondition(condition, player, isOffHandAttack));
    }

    private static boolean evaluateCondition(WeaponAttributes.Condition condition, PlayerEntity player, boolean isOffHandAttack) {
        switch (condition) {
            case DUAL_WIELDING_ANY -> {
                return isDualWielding(player);
            }
            case DUAL_WIELDING_SAME -> {
                return isDualWielding(player) &&
                        (player.getMainHandStack().getItem() == player.getOffHandStack().getItem()); // TODO: Eq
            }
            case MAIN_HAND_ONLY -> {
                return !isOffHandAttack;
            }
            case OFF_HAND_ONLY -> {
                return isOffHandAttack;
            }
        }
        return true;
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

    public static float getScaledAttackCooldown(PlayerEntity player) {
        // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownTicks`
        return player.getAttackCooldownProgressPerTick() / getDualWieldingAttackSpeedMultiplier(player);
    }
}
