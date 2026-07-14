package net.bettercombat.mixin.client;

import net.bettercombat.client.BetterCombatClientMod;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "handleParticleEvent", at = @At("HEAD"), cancellable = true)
    private void onParticle_Pre(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        if(!BetterCombatClientMod.config.isSweepingParticleEnabled
                && packet.getParticle().getType().equals(ParticleTypes.SWEEP_ATTACK)) {
            ci.cancel();
        }
    }
}
