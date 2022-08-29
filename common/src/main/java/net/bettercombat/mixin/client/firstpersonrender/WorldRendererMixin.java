package net.bettercombat.mixin.client.firstpersonrender;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.bettercombat.compatibility.CompatibilityFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Redirect(method = "render", at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/render" +
            "/Camera;" +
            "isThirdPerson()Z"))
    private boolean renderInFirstPerson(Camera instance) {
        if (!CompatibilityFlags.firstPersonRender() || MinecraftClient.getInstance().player.isSleeping()) {
            return instance.isThirdPerson();
        }
        return true;
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void dontRenderEntity_Begin(Entity entity, double cameraX, double cameraY, double cameraZ,
                                         float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (!CompatibilityFlags.firstPersonRender()) {
            // Do nothing -> Fallthrough (allow render)
            return;
        }

        if(entity instanceof PlayerEntity player) {
            if (player.isSleeping()) {
                return;
            }
        }

        Optional<IAnimation> currentAnimation;
        if (entity instanceof PlayerAttackAnimatable) {
            currentAnimation = ((PlayerAttackAnimatable) entity).getCurrentAnimation();
        } else {
            currentAnimation = Optional.empty();
        }
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        var isActive = false;
        if (currentAnimation.isPresent()) {
            isActive = currentAnimation.get().isActive();
            if (currentAnimation.get() instanceof IExtendedAnimation extendedAnimation) {
                isActive = extendedAnimation.isActiveInFirstPerson();
            }
        }

        if (entity == camera.getFocusedEntity() && !camera.isThirdPerson()) {
            if(isActive) {
                FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel = true;
                // Do nothing -> Fallthrough (allow render)
                return;
            } else {
                // Don't render anything
                ci.cancel();
            }
        } else {
            // Do nothing -> Fallthrough (allow render)
            return;
        }
    }

    @Inject(method = "renderEntity", at = @At("TAIL"), cancellable = true)
    private void dontRenderEntity_End(Entity entity, double cameraX, double cameraY, double cameraZ,
                                      float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (entity == camera.getFocusedEntity()) {
            FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel = false;
        }
    }
}
