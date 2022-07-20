package net.bettercombat.mixin.client.firstpersonrender;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.PlayerAttackAnimatable;
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
//        Optional<IAnimation> currentAnimation = ((PlayerAttackAnimatable) entity).getCurrentAnimation();
//        var isActive = false;
//        if (currentAnimation.isPresent()) {
//            isActive = currentAnimation.get().isActive();
//            if (currentAnimation.get() instanceof IExtendedAnimation extendedAnimation) {
//                isActive = extendedAnimation.isActiveInFirstPerson();
//            }
//        }
        // if (currentAnimation.isPresent() && isActive && entity == MinecraftClient.getInstance().player && !camera.isThirdPerson()) {
        if (entity == MinecraftClient.getInstance().player && !camera.isThirdPerson()) {
            this.model.head.visible = false;
            this.model.body.visible = false;
            this.model.leftLeg.visible = false;
            this.model.rightLeg.visible = false;
            this.model.rightArm.visible = showArms;
            this.model.leftArm.visible = showArms;

            this.model.leftSleeve.visible = false;
            this.model.rightSleeve.visible = false;
            this.model.leftPants.visible = false;
            this.model.rightPants.visible = false;
            this.model.jacket.visible = false;
//            this.model.cloak.visible = false; // private
//            this.model.ear.visible = false;  // private
        } else {
            this.model.head.visible = true;
            this.model.body.visible = true;
            this.model.leftLeg.visible = true;
            this.model.rightLeg.visible = true;
            this.model.rightArm.visible = true;
            this.model.leftArm.visible = true;

            this.model.hat.visible = true;
            this.model.leftSleeve.visible = true;
            this.model.rightSleeve.visible = true;
            this.model.leftPants.visible = true;
            this.model.rightPants.visible = true;
            this.model.jacket.visible = true;
        }
    }
}
