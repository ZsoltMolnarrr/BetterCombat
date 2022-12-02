package net.bettercombat.mixin;

import net.bettercombat.BetterCombat;
import net.minecraft.enchantment.SweepingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweepingEnchantment.class)
public class SweepingEnchantmentMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    public void getMaxLevel_DisableSweeping(CallbackInfoReturnable<Integer> cir) {
        if (BetterCombat.config.allow_sweeping) {
            return;
        }
        cir.setReturnValue(0);
        cir.cancel();
    }
}
