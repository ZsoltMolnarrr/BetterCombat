package net.bettercombat.mixin.client;

import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.collision.OrientedBoundingBox;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @Inject(method = "emitGizmos",at = @At("TAIL"))
    private void render_TAIL(double cameraX, double cameraY, double cameraZ, DebugValueAccess store, Frustum frustum, float tickProgress, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        if (!BetterCombatClientMod.config.isDebugOBBEnabled) {
            return;
        }
        Camera camera = client.gameRenderer.getMainCamera();
        if (!camera.isInitialized()) {
            return;
        }
        if (client.player.getMainHandItem() == null) {
            return;
        }
        var extendedClient = (MinecraftClient_BetterCombat) client;
        var comboCount = extendedClient.getComboCount();
        var hand = PlayerAttackHelper.getCurrentAttack(client.player, comboCount);
        if (hand == null) {
            return;
        }
        WeaponAttributes attributes = hand.attributes();
        if (attributes == null) {
            return;
        }
        var cursorTarget = extendedClient.getCursorTarget();
        var range = PlayerAttackHelper.getRangeForItem(player, hand.itemStack());
        range *= hand.attack().rangeMultiplier();
        var target = TargetFinder.findAttackTargetResult(
                player,
                cursorTarget,
                hand.attack(),
                range);
        boolean collides = target.entities.size() > 0;

        // Calculate interpolation offset for smooth rendering
        // The OBB is calculated based on tick-based position, but we need to render at interpolated position
        Vec3 playerPos = player.position();
        Vec3 interpolatedPos = player.getPosition(tickProgress);
        Vec3 interpolationOffset = interpolatedPos.subtract(playerPos);

        // Draw the attack OBB with interpolation
        var obb = target.obb.copy();
        obb.center = obb.center.add(interpolationOffset);
        obb.updateVertex();

        int obbColor = collides
                ? ARGB.colorFromFloat(1.0F, 1.0F, 0.0F, 0.0F)  // Red with full alpha
                : ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F); // Green with full alpha

        drawOBB(obb, obbColor);

        // Draw colliding entity boxes with interpolation
        int entityColor = ARGB.colorFromFloat(1.0F, 1.0F, 0.0F, 0.0F); // Red with full alpha
        for (var entity : target.entities) {
            Vec3 entityPos = entity.position();
            Vec3 entityInterpolatedPos = entity.getPosition(tickProgress);
            Vec3 entityInterpolationOffset = entityInterpolatedPos.subtract(entityPos);

            var entityObb = new OrientedBoundingBox(entity.getBoundingBox())
                    .scale(0.95);
            entityObb.center = entityObb.center.add(entityInterpolationOffset);
            entityObb.updateVertex();

            drawOBB(entityObb, entityColor);
        }
    }

    private void drawOBB(OrientedBoundingBox obb, int color) {
        // Bottom rectangle (vertices 1, 2, 6, 5)
        Gizmos.line(obb.vertex1, obb.vertex2, color);
        Gizmos.line(obb.vertex2, obb.vertex6, color);
        Gizmos.line(obb.vertex6, obb.vertex5, color);
        Gizmos.line(obb.vertex5, obb.vertex1, color);

        // Top rectangle (vertices 4, 3, 7, 8)
        Gizmos.line(obb.vertex4, obb.vertex3, color);
        Gizmos.line(obb.vertex3, obb.vertex7, color);
        Gizmos.line(obb.vertex7, obb.vertex8, color);
        Gizmos.line(obb.vertex8, obb.vertex4, color);

        // Vertical edges connecting bottom to top
        Gizmos.line(obb.vertex1, obb.vertex4, color);
        Gizmos.line(obb.vertex2, obb.vertex3, color);
        Gizmos.line(obb.vertex6, obb.vertex7, color);
        Gizmos.line(obb.vertex5, obb.vertex8, color);
    }
}
