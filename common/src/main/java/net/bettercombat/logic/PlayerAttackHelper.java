package net.bettercombat.logic;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.utils.AttributeModifierHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PlayerAttackHelper {
    public static float getDualWieldingAttackDamageMultiplier(Player player, AttackHand hand) {
        return isDualWielding(player)
                ? (hand.isOffHand()
                    ? BetterCombatMod.config.dual_wielding_off_hand_damage_multiplier
                    : BetterCombatMod.config.dual_wielding_main_hand_damage_multiplier)
                : 1;
    }

    public static boolean shouldAttackWithOffHand(Player player, int comboCount) {
        return PlayerAttackHelper.isDualWielding(player) && comboCount % 2 == 1;
    }

    public static boolean isDualWielding(Player player) {
        var mainAttributes = WeaponRegistry.getAttributes(player.getMainHandItem());
        var offAttributes = WeaponRegistry.getAttributes(player.getOffhandItem());
        return isDualWielding(mainAttributes, offAttributes);
    }

    public static boolean isDualWielding(WeaponAttributes mainAttributes, WeaponAttributes offAttributes) {
        return mainAttributes != null && !mainAttributes.isTwoHanded()
                && offAttributes != null && !offAttributes.isTwoHanded();
    }

    public static boolean isTwoHandedWielding(Player player) {
        var mainAttributes = WeaponRegistry.getAttributes(player.getMainHandItem());
        if (mainAttributes != null) {
            return mainAttributes.isTwoHanded();
        }
        return false;
    }

    public static float getAttackCooldownTicksCapped(Player player) {
        // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownLengthTicks`
        return Math.max(player.getCurrentItemAttackStrengthDelay(), BetterCombatMod.config.attack_interval_cap);
    }

    @Nullable
    public static AttackHand getCurrentAttack(Player player, int comboCount) {
        if (isDualWielding(player)) {
            boolean isOffHand = shouldAttackWithOffHand(player,comboCount);
            var itemStack = isOffHand
                    ? player.getOffhandItem()
                    : player.getMainHandItem();
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
            var itemStack = player.getMainHandItem();
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
    private static AttackSelection selectAttack(int comboCount, WeaponAttributes attributes, Player player, boolean isOffHandAttack) {
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

    private static boolean evaluateConditions(WeaponAttributes.Condition[] conditions, Player player, boolean isOffHandAttack) {
        return Arrays.stream(conditions).allMatch(condition -> evaluateCondition(condition, player, isOffHandAttack));
    }

    private static boolean evaluateCondition(WeaponAttributes.Condition condition, Player player, boolean isOffHandAttack) {
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
                        (player.getMainHandItem().getItem() == player.getOffhandItem().getItem());
            }
            case DUAL_WIELDING_SAME_CATEGORY -> {
                if (!isDualWielding(player)) {
                    return false;
                }
                var mainHandAttributes = WeaponRegistry.getAttributes(player.getMainHandItem());
                var offHandAttributes = WeaponRegistry.getAttributes(player.getOffhandItem());
                if (mainHandAttributes.category() == null
                        || mainHandAttributes.category().isEmpty()
                        || offHandAttributes.category() == null
                        || offHandAttributes.category().isEmpty()) {
                    return false;
                }
                return mainHandAttributes.category().equals(offHandAttributes.category());
            }
            case NO_OFFHAND_ITEM -> {
                var offhandStack = player.getOffhandItem();
                if(offhandStack == null || offhandStack.isEmpty()) {{
                    return true;
                }}
                return false;
            }
            case OFF_HAND_SHIELD -> {
                var offhandStack = player.getOffhandItem();
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

    public static void swapHandAttributes(Player player, Runnable runnable) {
        swapHandAttributes(player, true, runnable);
    }

    public static void swapHandAttributes(Player player, boolean useOffHand, Runnable runnable) {
        if (!useOffHand) {
            runnable.run();
            return;
        }
        synchronized (player) {
            var inventory = player.getInventory();
            var mainHandStack = player.getMainHandItem();
            var offHandStack = InventoryUtil.getOffHandSlotStack(player);

            setAttributesForOffHandAttack(player, true);
            inventory.setSelectedItem(offHandStack);
            InventoryUtil.setOffHandSlotStack(player, mainHandStack);

            runnable.run();

            inventory.setSelectedItem(mainHandStack);
            InventoryUtil.setOffHandSlotStack(player, offHandStack);
            setAttributesForOffHandAttack(player, false);
        }
    }

    private static void setAttributesForOffHandAttack(Player player, boolean useOffHand) {
        var mainHandStack = player.getMainHandItem();
        var offHandStack = player.getOffhandItem();
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
            player.getAttributes().removeAttributeModifiers(modifiersMap);
        }
        if (add != null) {
            var modifiersMap = AttributeModifierHelper.modifierMultimap(add);
            player.getAttributes().addTransientAttributeModifiers(modifiersMap);
        }
    }

    public static Pose poseForPlayer(Player player) {
        var mainHandStack = player.getMainHandItem();
        var mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
        String mainPose;
        if (mainHandAttributes != null && mainHandAttributes.pose() != null) {
            mainPose = mainHandAttributes.pose();
        } else {
            mainPose = "";
        }
        var offHandStack = player.getOffhandItem();
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



    public static double getStaticRange(Player player, ItemStack stack) {
        var attributes = WeaponRegistry.getAttributes(stack);
        return combineAttackRange(attributes, player.getAttributeBaseValue(Attributes.ENTITY_INTERACTION_RANGE));
    }

    public static double getRangeForItem(Player player, ItemStack stack) {
        var interactionRangeValue = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
        return getRangeWithItem(stack, interactionRangeValue);
    }

    public static double getRangeWithWeapon(Player player, double interactionRangeValue) {
        return getRangeWithItem(player.getMainHandItem(), interactionRangeValue);
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
