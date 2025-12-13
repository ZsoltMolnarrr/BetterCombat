package net.bettercombat.logic;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.utils.AttributeModifierHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PlayerAttackHelper {
    public static float getDualWieldingAttackDamageMultiplier(PlayerEntity player, AttackHand hand) {
        return isDualWielding(player)
                ? (hand.isOffHand()
                    ? BetterCombatMod.config.dual_wielding_off_hand_damage_multiplier
                    : BetterCombatMod.config.dual_wielding_main_hand_damage_multiplier)
                : 1;
    }

    public static boolean shouldAttackWithOffHand(PlayerEntity player, int comboCount) {
        return PlayerAttackHelper.isDualWielding(player) && comboCount % 2 == 1;
    }

    public static boolean isDualWielding(PlayerEntity player) {
        var mainAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        var offAttributes = WeaponRegistry.getAttributes(player.getOffHandStack());
        return isDualWielding(mainAttributes, offAttributes);
    }

    public static boolean isDualWielding(WeaponAttributes mainAttributes, WeaponAttributes offAttributes) {
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

    public static float getAttackCooldownTicksCapped(PlayerEntity player) {
        // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownLengthTicks`
        return Math.max(player.getAttackCooldownProgressPerTick(), BetterCombatMod.config.attack_interval_cap);
    }

    @Nullable
    public static AttackHand getCurrentAttack(PlayerEntity player, int comboCount) {
        if (isDualWielding(player)) {
            boolean isOffHand = shouldAttackWithOffHand(player,comboCount);
            var itemStack = isOffHand
                    ? player.getOffHandStack()
                    : player.getMainHandStack();
            var attributes = WeaponRegistry.getAttributes(itemStack);
            if (attributes != null && attributes.attacks() != null) {
                int handSpecificComboCount = ((isOffHand && comboCount > 0) ? (comboCount - 1) : (comboCount)) / 2;
                var attackSelection = selectAttack(handSpecificComboCount, attributes, player, isOffHand);
                if (attackSelection == null) {
                    return null;
                }
                var attack = attackSelection.attack;
                var combo = attackSelection.comboState;
                return new AttackHand(attack, combo, isOffHand, attributes, itemStack);
            }
        } else {
            var itemStack = player.getMainHandStack();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
            if (attributes != null && attributes.attacks() != null) {
                var attackSelection = selectAttack(comboCount, attributes, player, false);
                if (attackSelection == null) {
                    return null;
                }
                var attack = attackSelection.attack;
                var combo = attackSelection.comboState;
                return new AttackHand(attack, combo, false, attributes, itemStack);
            }
        }
        return null;
    }

    private record AttackSelection(WeaponAttributes.Attack attack, ComboState comboState) { }

    @Nullable
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
        if (attacks.length == 0) {
            return null;
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
            case MOUNTED -> {
                return player.getVehicle() != null;
            }
            case NOT_MOUNTED -> {
                return player.getVehicle() == null;
            }
        }
        return true;
    }

    private static final Object attributesLock = new Object();

    public static void swapHandAttributes(PlayerEntity player, Runnable runnable) {
        swapHandAttributes(player, true, runnable);
    }

    public static void swapHandAttributes(PlayerEntity player, boolean useOffHand, Runnable runnable) {
        if (!useOffHand) {
            runnable.run();
            return;
        }
        synchronized (player) {
            var inventory = player.getInventory();
            var mainHandStack = player.getMainHandStack();
            var offHandStack = InventoryUtil.getOffHandSlotStack(player);

            setAttributesForOffHandAttack(player, true);
            inventory.setSelectedStack(offHandStack);
            InventoryUtil.setOffHandSlotStack(player, mainHandStack);

            runnable.run();

            inventory.setSelectedStack(mainHandStack);
            InventoryUtil.setOffHandSlotStack(player, offHandStack);
            setAttributesForOffHandAttack(player, false);
        }
    }

    private static void setAttributesForOffHandAttack(PlayerEntity player, boolean useOffHand) {
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
            var modifiersMap = AttributeModifierHelper.modifierMultimap(remove);
            player.getAttributes().removeModifiers(modifiersMap);
        }
        if (add != null) {
            var modifiersMap = AttributeModifierHelper.modifierMultimap(add);
            player.getAttributes().addTemporaryModifiers(modifiersMap);
        }
    }

    public static Pose poseForPlayer(PlayerEntity player) {
        var mainHandStack = player.getMainHandStack();
        var mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
        String mainPose;
        if (mainHandAttributes != null && mainHandAttributes.pose() != null) {
            mainPose = mainHandAttributes.pose();
        } else {
            mainPose = "";
        }
        var offHandStack = player.getOffHandStack();
        var offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
        String offPose;
        if (PlayerAttackHelper.isDualWielding(mainHandAttributes, offHandAttributes)
                && offHandAttributes != null && offHandAttributes.pose() != null) {
            offPose = offHandAttributes.pose();
        } else {
            offPose = "";
        }
        return new Pose(mainPose, offPose);
    }



    public static double getStaticRange(PlayerEntity player, ItemStack stack) {
        var attributes = WeaponRegistry.getAttributes(stack);
        return combineAttackRange(attributes, player.getAttributeBaseValue(EntityAttributes.ENTITY_INTERACTION_RANGE));
    }

    public static double getRangeForItem(PlayerEntity player, ItemStack stack) {
        var interactionRangeValue = player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE);
        return getRangeWithItem(stack, interactionRangeValue);
    }

    public static double getRangeWithWeapon(PlayerEntity player, double interactionRangeValue) {
        return getRangeWithItem(player.getMainHandStack(), interactionRangeValue);
    }

    private static double getRangeWithItem(ItemStack stack, double interactionRangeValue) {
        if (EntityAttributeHelper.itemHasRangeAttribute(stack)) {
            return interactionRangeValue;
        }
        var attributes = WeaponRegistry.getAttributes(stack);
        return combineAttackRange(attributes, interactionRangeValue);
    }

    public static double combineAttackRange(WeaponAttributes attributes, double interactionRangeValue) {
        var range = interactionRangeValue;
        if (attributes != null) {
            // Absolute range (legacy)
            if (attributes.attackRange() != 0) {
                return attributes.attackRange();
            }
            range += attributes.rangeBonus();
        }
        return range;
    }
}
