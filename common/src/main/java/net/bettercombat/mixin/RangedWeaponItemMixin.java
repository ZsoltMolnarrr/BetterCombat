package net.bettercombat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.BetterCombat;
import net.bettercombat.logic.CombatMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {

    /**
     * FEATURE: Two-handed wielding
     *
     * Two-handed weapons (such as Heavy Crossbow) disable offhand slot.
     * Disabled offhand slot returns `EMPTY` stack when queried using:
     * - getStackInHand(Hand.OFF_HAND)
     * - getOffHandStack()
     *
     * This causes two-handed ranged weapons to no longer prioritize offhand projectiles.
     * This mixin fixes that.
     */

    @WrapOperation(
            method = "getHeldProjectile",
            require = 0, // Make this optional, it is not worth crashing the game over
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getHeldProjectile_Wrapped_BetterCombat(
            // Mixin Parameters
            LivingEntity entity, Hand hand, Operation<ItemStack> original
            // Context Parameters (not needed)
            // LivingEntity entity, Predicate<ItemStack> predicate
    ) {
        var originalResult = original.call(entity, hand); // Always call original first to allow others' side effects
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return originalResult;
        if (entity instanceof PlayerEntity player) {
            return player.getInventory().offHand.get(0);
        } else {
            return originalResult;
        }
    }
}
