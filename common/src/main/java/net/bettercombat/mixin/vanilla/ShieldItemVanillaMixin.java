package net.bettercombat.mixin.vanilla;

import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.CombatMode;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShieldItem.class)
public abstract class ShieldItemVanillaMixin {
    @Inject(method = "use", at = @At("HEAD"))
    protected void useItem(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient() || BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) {
            return;
        }
        var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombat.config.upswing_multiplier));
        ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
    }
}
