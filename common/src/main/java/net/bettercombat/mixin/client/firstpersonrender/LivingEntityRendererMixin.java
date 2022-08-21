package net.bettercombat.mixin.client.firstpersonrender;

import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Shadow List<Object> features;

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;features:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<Object> getFeaturesConditionally(LivingEntityRenderer renderer) {
        if (FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel) {
            return features.stream()
                    .filter( item -> {
                        return item instanceof PlayerHeldItemFeatureRenderer;
                    }).collect(Collectors.toList());
        } else {
            return features;
        }
    }
}