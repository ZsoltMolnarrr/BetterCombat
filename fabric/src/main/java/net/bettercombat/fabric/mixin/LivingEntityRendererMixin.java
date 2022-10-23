package net.bettercombat.fabric.mixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

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
            // Only for IExtendedAnimation (weapon swings)
            var isActive = false; // currentAnimation.get().isActive();
            if (currentAnimation.get() instanceof IExtendedAnimation extendedAnimation) {
                isActive = extendedAnimation.isActiveInFirstPerson();
            }

            if (isActive) {
                args.set(3, MathHelper.lerpAngleDegrees(args.get(4), entity.prevHeadYaw, entity.headYaw));
            }
        }
    }
}
