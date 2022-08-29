package net.bettercombat.mixin.client.firstpersonrender;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.client.animation.IExtendedAnimation;
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

import java.util.Optional;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at = @At("HEAD"), cancellable = true)
    private void dontRenderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers,
                                ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (!CompatibilityFlags.firstPersonRender()) {
            return;
        }
        Optional<IAnimation> currentAnimation = ((PlayerAttackAnimatable) player).getCurrentAnimation();
        if (currentAnimation.isPresent()) {
            var isActive = currentAnimation.get().isActive();
            if (currentAnimation.get() instanceof IExtendedAnimation extendedAnimation) {
                isActive = extendedAnimation.isActiveInFirstPerson();
            }

            if (isActive) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void renderItem_HEAD(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (entity != player) {
            return;
        }
        if (FirstPersonRenderHelper.isRenderingFirstPersonPlayerModel) {
            if (!BetterCombatClient.config.isShowingOtherHandFirstPerson) {
                var isMainHandStack = player.getMainHandStack() == stack;
                if (FirstPersonRenderHelper.isAttackingWithOffHand) {
                    if (isMainHandStack) {
                        ci.cancel();
                    }
                } else {
                    if (!isMainHandStack) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
