package ca.lukegrahamlandry.bettercombat.mixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: pls fix me elegantly :)
// the modify args in render -> setupTransforms causes java.lang.NoClassDefFoundError: org/spongepowered/asm/synthetic/args/Args$1
// maybe a forge patch problem? i really have no idea
// so im just injecting to replace the body of setupTransforms entirly instead
// this feels like an ugly solution.
// also i think my version makes it jitter horribly at the start of each anim in first person
@Mixin(LivingEntityRenderer.class)
public abstract class ForgeLivingEntityRendererMixin {
    @Shadow protected abstract float getLyingAngle(LivingEntity entity);

    @Shadow protected abstract boolean isShaking(LivingEntity entity);

    @Inject(method = "setupTransforms", at = @At(value = "HEAD"), cancellable = true)
    private void setupTransforms(LivingEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
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
                bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw);
            }
        }

        //////////////////////////////////////////////////////////////////////////////////////////////

        if (this.isShaking(entity)) {
            bodyYaw += (float)(Math.cos((double)((LivingEntity)entity).age * 3.25) * Math.PI * (double)0.4f);
        }
        if (!((Entity)entity).isInPose(EntityPose.SLEEPING)) {
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - bodyYaw));
        }
        if (((LivingEntity)entity).deathTime > 0) {
            float f = ((float)((LivingEntity)entity).deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * this.getLyingAngle(entity)));
        } else if (((LivingEntity)entity).isUsingRiptide()) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f - ((Entity)entity).getPitch()));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(((float)((LivingEntity)entity).age + tickDelta) * -75.0f));
        } else if (((Entity)entity).isInPose(EntityPose.SLEEPING)) {
            Direction direction = ((LivingEntity)entity).getSleepingDirection();
            float f1 = direction != null ? getYaw(direction) : bodyYaw;
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f1));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(entity)));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
        } else if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
            matrices.translate(0.0, ((Entity)entity).getHeight() + 0.1f, 0.0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        }

        ci.cancel();
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
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
