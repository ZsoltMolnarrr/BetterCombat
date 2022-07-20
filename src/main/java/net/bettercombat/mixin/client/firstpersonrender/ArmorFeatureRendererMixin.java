package net.bettercombat.mixin.client.firstpersonrender;

import net.bettercombat.client.animation.FirstPersonRenderHelper;
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

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void dontRenderArmor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                                          int i, T entity, float f, float g, float h, float j, float k,
                                                          float l, CallbackInfo ci) {
        if (entity == MinecraftClient.getInstance().player && FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel) {
            ci.cancel();
        }
    }
}
