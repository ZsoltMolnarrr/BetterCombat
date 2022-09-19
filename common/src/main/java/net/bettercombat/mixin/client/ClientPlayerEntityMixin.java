package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombat;
import net.bettercombat.client.MathHelper;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void tickMovement_ModifyInput(CallbackInfo ci) {
        var config = BetterCombat.config;
        var multiplier = Math.min(Math.max(config.movement_speed_while_attacking, 0.0), 1.0);
//        System.out.println("Multiplier " + multiplier);
        if (multiplier == 1) {
            return;
        }
        var clientPlayer = (ClientPlayerEntity)((Object)this);
        if (clientPlayer.hasVehicle() && !config.movement_speed_effected_while_mounting) {
            return;
        }
        var client = (MinecraftClient_BetterCombat) MinecraftClient.getInstance();
        var swingProgress = client.getSwingProgress();
        if (swingProgress < 0.98) {
            if (config.movement_speed_applied_smoothly) {
                double p2 = 0;
                if (swingProgress <= 0.5) {
                    p2 = MathHelper.easeOutCubic(swingProgress * 2);
                } else {
                    p2 = MathHelper.easeOutCubic(1 - ((swingProgress - 0.5) * 2));
                }
                multiplier = (float) ( 1.0 - (1.0 - multiplier) * p2 );
//                var chart = "-".repeat((int)(100.0 * multiplier)) + "x";
//                System.out.println("Movement speed multiplier: " + String.format("%.4f", multiplier) + ">" + chart);
            }
            clientPlayer.input.movementForward *= multiplier;
            clientPlayer.input.movementSideways *= multiplier;
        }
    }
}
