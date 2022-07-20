package net.bettercombat.mixin.client.firstpersonrender;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.client.animation.IExtendedAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity,
        PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx,
                                     PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void hideBonesInFirstPerson(AbstractClientPlayerEntity entity,
                                        float f, float g, MatrixStack matrixStack,
                                        VertexConsumerProvider vertexConsumerProvider,
                                        int i, CallbackInfo ci) {
        var showArms = BetterCombatClient.config.isShowingArmsInFirstPerson;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Optional<IAnimation> currentAnimation = ((PlayerAttackAnimatable) entity).getCurrentAnimation();
        var isActive = false;
        if (currentAnimation.isPresent()) {
            isActive = currentAnimation.get().isActive();
//            if (currentAnimation.get() instanceof IExtendedAnimation extendedAnimation) {
//                isActive = extendedAnimation.isActiveInFirstPerson();
//            }
        }

        if (entity == MinecraftClient.getInstance().player  && !camera.isThirdPerson()) {
            if (FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel || FirstPersonRenderHelper.isRenderingThirdPersonPlayerModel) {
                setPartsVisible(false);
            }
            if (FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel) {
                this.model.rightArm.visible = showArms;
                this.model.leftArm.visible = showArms;
            }
        } else {
            setPartsVisible(true);
        }
    }

    private void setPartsVisible(boolean visible) {
        this.model.head.visible = visible;
        this.model.body.visible = visible;
        this.model.leftLeg.visible = visible;
        this.model.rightLeg.visible = visible;
        this.model.rightArm.visible = visible;
        this.model.leftArm.visible = visible;

        this.model.hat.visible = visible;
        this.model.leftSleeve.visible = visible;
        this.model.rightSleeve.visible = visible;
        this.model.leftPants.visible = visible;
        this.model.rightPants.visible = visible;
        this.model.jacket.visible = visible;
    }
}
