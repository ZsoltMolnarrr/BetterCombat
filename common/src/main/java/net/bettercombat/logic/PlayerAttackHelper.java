package net.bettercombat.logic;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.Arrays;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;

public class PlayerAttackHelper {
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

    public static boolean isTwoHandedWielding(PlayerEntity player) {
        var mainAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        if (mainAttributes != null) {
            return mainAttributes.isTwoHanded();
        }
        return false;
    }

    public static AttackHand getCurrentAttack(PlayerEntity player, int comboCount) {
        if (isDualWielding(player)) {
            boolean isOffHand = shouldAttackWithOffHand(player,comboCount);
            var itemStack = isOffHand
                    ? player.getOffHandStack()
                    : player.getMainHandStack();
            var attributes = WeaponRegistry.getAttributes(itemStack);
            int handSpecificComboCount = ( (isOffHand && comboCount > 0) ? (comboCount - 1) : (comboCount) ) / 2;
            var attackSelection = selectAttack(handSpecificComboCount, attributes, player, isOffHand);
            var attack = attackSelection.attack;
            var combo = attackSelection.comboState;
            return new AttackHand(attack, combo, isOffHand, attributes, itemStack);
        } else {
            var itemStack = player.getMainHandStack();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
            if (attributes != null) {
                var attackSelection = selectAttack(comboCount, attributes, player, false);
                var attack = attackSelection.attack;
                var combo = attackSelection.comboState;
                return new AttackHand(attack, combo, false, attributes, itemStack);
            }
        }
        return null;
    }

    private record AttackSelection(WeaponAttributes.Attack attack, ComboState comboState) { }

    private static AttackSelection selectAttack(int comboCount, WeaponAttributes attributes, PlayerEntity player, boolean isOffHandAttack) {
        var attacks = attributes.attacks();
        attacks = Arrays.stream(attacks)
                .filter(attack ->
                        attack.conditions() == null
                        || attack.conditions().length == 0
                        || evaluateConditions(attack.conditions(), player, isOffHandAttack)
                )
                .toArray(WeaponAttributes.Attack[]::new);
        if (comboCount < 0) {
            comboCount = 0;
        }
        int index = comboCount % attacks.length;
        return new AttackSelection(attacks[index], new ComboState(index + 1, attacks.length));
    }

    private static boolean evaluateConditions(WeaponAttributes.Condition[] conditions, PlayerEntity player, boolean isOffHandAttack) {
        return Arrays.stream(conditions).allMatch(condition -> evaluateCondition(condition, player, isOffHandAttack));
    }

    private static boolean evaluateCondition(WeaponAttributes.Condition condition, PlayerEntity player, boolean isOffHandAttack) {
        if (condition == null) {
            return true;
        }
        switch (condition) {
            case NOT_DUAL_WIELDING -> {
                return !isDualWielding(player);
            }
            case DUAL_WIELDING_ANY -> {
                return isDualWielding(player);
            }
            case DUAL_WIELDING_SAME -> {
                return isDualWielding(player) &&
                        (player.getMainHandStack().getItem() == player.getOffHandStack().getItem());
            }
            case DUAL_WIELDING_SAME_CATEGORY -> {
                if (!isDualWielding(player)) {
                    return false;
                }
                var mainHandAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
                var offHandAttributes = WeaponRegistry.getAttributes(player.getOffHandStack());
                if (mainHandAttributes.category() == null
                        || mainHandAttributes.category().isEmpty()
                        || offHandAttributes.category() == null
                        || offHandAttributes.category().isEmpty()) {
                    return false;
                }
                return mainHandAttributes.category().equals(offHandAttributes.category());
            }
            case NO_OFFHAND_ITEM -> {
                var offhandStack = player.getOffHandStack();
                if(offhandStack == null || offhandStack.isEmpty()) {{
                    return true;
                }}
                return false;
            }
            case OFF_HAND_SHIELD -> {
                var offhandStack = player.getOffHandStack();
                if(offhandStack != null || offhandStack.getItem() instanceof ShieldItem) {{
                    return true;
                }}
                return false;
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
}
