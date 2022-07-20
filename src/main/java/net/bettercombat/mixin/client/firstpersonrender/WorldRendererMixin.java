package net.bettercombat.mixin.client.firstpersonrender;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ,
                                         float tickDelta,
                                         MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Redirect(method = "render", at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/render" +
            "/Camera;" +
            "isThirdPerson()Z"))
    private boolean renderInFirstPerson(Camera instance) {
        return true;
    }

    //This should probably be replaced with a better injection, possibly WrapWithCondition from MixinExtras
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private void dontRenderEntity(WorldRenderer instance, Entity entity, double cameraX, double cameraY, double cameraZ,
                                  float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
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
                renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, vertexConsumers);
                FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel = false;
            }
        } else {
            renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, vertexConsumers);
        }
    }
}
