package net.bettercombat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.logic.InventoryUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileWeaponItem.class)
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack getHeldProjectile_Wrapped_BetterCombat(
            // Mixin Parameters
            LivingEntity entity, InteractionHand hand, Operation<ItemStack> original
            // Context Parameters (not needed)
            // LivingEntity entity, Predicate<ItemStack> predicate
    ) {
        var originalResult = original.call(entity, hand); // Always call original first to allow others' side effects
        if (entity instanceof Player player) {
            return InventoryUtil.getOffHandSlotStack(player);
        } else {
            return originalResult;
        }
    }
}
