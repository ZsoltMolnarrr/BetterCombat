package net.bettercombat.mixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void dontRenderArmor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                                          int i, T entity, float f, float g, float h, float j, float k,
                                                          float l, CallbackInfo ci) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (entity == MinecraftClient.getInstance().player && !camera.isThirdPerson()) {
            ci.cancel();
        }
    }
}
