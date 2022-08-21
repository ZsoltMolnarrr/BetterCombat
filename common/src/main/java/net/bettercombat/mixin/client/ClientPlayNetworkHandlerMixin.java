package net.bettercombat.mixin.client;

import net.bettercombat.client.BetterCombatClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void onParticle_Pre(ParticleS2CPacket packet, CallbackInfo ci) {
        if(!BetterCombatClient.config.isSweepingParticleEnabled) {
            ci.cancel();
        }
    }
}
