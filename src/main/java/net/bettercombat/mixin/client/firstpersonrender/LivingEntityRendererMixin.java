package net.bettercombat.mixin.client.firstpersonrender;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V"))
    private void modifyArg(Args args) {
        LivingEntity entity = args.get(0);
        Optional<IAnimation> currentAnimation = Optional.empty();
        if (entity instanceof PlayerAttackAnimatable) {
            currentAnimation = ((PlayerAttackAnimatable) entity).getCurrentAnimation();
        }
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        //When in first person make the animation play facing forward, instead of slowly adjusting to player rotations
        if (currentAnimation.isPresent()
                && entity == MinecraftClient.getInstance().player
                && !camera.isThirdPerson()
        ) {
            var isActive = currentAnimation.get().isActive();
            if (currentAnimation.get() instanceof IExtendedAnimation extendedAnimation) {
                isActive = extendedAnimation.isActiveInFirstPerson();
            }

            if (isActive) {
                args.set(3, MathHelper.lerpAngleDegrees(args.get(4), entity.prevHeadYaw, entity.headYaw));
            }
        }
    }

    @Shadow List<Object> features;

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;features:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<Object> getFeaturesConditionally(LivingEntityRenderer renderer) {
        if (FirstPersonRenderHelper.isFeatureEnabled && FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel) {
            return features.stream()
                    .filter( item -> {
                        return item instanceof PlayerHeldItemFeatureRenderer;
                    }).collect(Collectors.toList());
        } else {
            return features;
        }
    }
}
