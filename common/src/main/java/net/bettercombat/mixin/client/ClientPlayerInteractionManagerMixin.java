package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "cancelBlockBreaking", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;resetTicksSince()V",
            shift = At.Shift.AFTER))
    public void cancelBlockBreaking_FixAttackCD(CallbackInfo ci) {
        try {
            var player = client.player;
            var cooldownLength = PlayerAttackHelper.getAttackCooldownTicksCapped(player); // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownLengthTicks`
            float typicalUpswing = 0.5F;
            int reducedCooldown = Math.round(cooldownLength * typicalUpswing * BetterCombatMod.config.upswing_multiplier);
            ((LivingEntityAccessor)player).betterCombat_setTicksSinceLastAttack(reducedCooldown);
        } catch (Exception ignored) { } // We may get random exceptions when trying to access weapon cooldown
    }
}
