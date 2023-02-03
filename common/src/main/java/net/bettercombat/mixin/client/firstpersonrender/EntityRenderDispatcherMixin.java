package net.bettercombat.mixin.client.firstpersonrender;

import net.bettercombat.client.animation.first_person.FirstPersonRenderHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void pre_renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        if (FirstPersonRenderHelper.isRenderCycleFirstPerson()) {
            // Shadow doesn't render in first person,
            // so we don't want to make it appear during first person animation
            ci.cancel();
        }
    }
}
