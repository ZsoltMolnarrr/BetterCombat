package net.bettercombat.mixin.client.firstpersonrender;

import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        if (!FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel) {
            return;
        }

        var showArms = BetterCombatClient.config.isShowingArmsInFirstPerson;
        if (entity == MinecraftClient.getInstance().player) {
            var player = MinecraftClient.getInstance().player;
            setPartsVisible(false);
            var showRightArm = showArms;
            var showLeftArm = showArms;
            if (!BetterCombatClient.config.isShowingOtherHandFirstPerson) {
                showRightArm = showRightArm && !FirstPersonRenderHelper.isAttackingWithOffHand;
                showLeftArm = showLeftArm && FirstPersonRenderHelper.isAttackingWithOffHand;
            }
            if (entity.getMainArm() == Arm.LEFT) {
                var rightValue = showRightArm;
                var leftValue = showLeftArm;
                showRightArm = leftValue;
                showLeftArm = rightValue;
            }
            this.model.rightArm.visible = showRightArm;
            this.model.rightSleeve.visible = showRightArm && player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            this.model.leftArm.visible = showLeftArm;
            this.model.leftSleeve.visible = showLeftArm && player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
        }
        // No `else` case needed to show parts, since the default state should be correct already
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