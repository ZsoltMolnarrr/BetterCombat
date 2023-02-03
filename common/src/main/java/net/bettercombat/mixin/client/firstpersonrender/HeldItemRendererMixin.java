package net.bettercombat.mixin.client.firstpersonrender;

import net.bettercombat.client.animation.first_person.FirstPersonAnimator;
import net.bettercombat.client.animation.first_person.FirstPersonRenderState;
import net.bettercombat.compatibility.CompatibilityFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at = @At("HEAD"), cancellable = true)
    private void dontRenderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers,
                                ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (!CompatibilityFlags.firstPersonRender()) {
            return;
        }
        var currentAnimation = ((FirstPersonAnimator) player).getActiveFirstPersonAnimation(tickDelta);
        if (currentAnimation.isPresent()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void renderItem_HEAD(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity != MinecraftClient.getInstance().getCameraEntity()) {
            return;
        }
        if (FirstPersonRenderState.isRenderCycleFirstPerson()) {
            var animation = FirstPersonRenderState.getRenderCycleData();
            var isMainHandStack = entity.getMainHandStack() == stack;
            // Hiding held items based on config
            if (isMainHandStack) {
                if (!animation.config().showRightItem()) {
                    ci.cancel();
                }
            } else {
                if (!animation.config().showLeftItem()) {
                    ci.cancel();
                }
            }
        }
    }
}
