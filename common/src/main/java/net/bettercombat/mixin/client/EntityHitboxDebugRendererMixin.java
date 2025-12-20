package net.bettercombat.mixin.client;

import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.collision.OrientedBoundingBox;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @Inject(method = "render",at = @At("TAIL"))
    private void render_TAIL(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (!BetterCombatClientMod.config.isDebugOBBEnabled) {
            return;
        }
        Camera camera = client.gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        if (client.player.getMainHandStack() == null) {
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
        Vec3d playerPos = player.getEntityPos();
        Vec3d interpolatedPos = player.getLerpedPos(tickProgress);
        Vec3d interpolationOffset = interpolatedPos.subtract(playerPos);

        // Draw the attack OBB with interpolation
        var obb = target.obb.copy();
        obb.center = obb.center.add(interpolationOffset);
        obb.updateVertex();

        int obbColor = collides
                ? ColorHelper.fromFloats(1.0F, 1.0F, 0.0F, 0.0F)  // Red with full alpha
                : ColorHelper.fromFloats(1.0F, 0.0F, 1.0F, 0.0F); // Green with full alpha

        drawOBB(obb, obbColor);

        // Draw colliding entity boxes with interpolation
        int entityColor = ColorHelper.fromFloats(1.0F, 1.0F, 0.0F, 0.0F); // Red with full alpha
        for (var entity : target.entities) {
            Vec3d entityPos = entity.getEntityPos();
            Vec3d entityInterpolatedPos = entity.getLerpedPos(tickProgress);
            Vec3d entityInterpolationOffset = entityInterpolatedPos.subtract(entityPos);

            var entityObb = new OrientedBoundingBox(entity.getBoundingBox())
                    .scale(0.95);
            entityObb.center = entityObb.center.add(entityInterpolationOffset);
            entityObb.updateVertex();

            drawOBB(entityObb, entityColor);
        }
    }

    private void drawOBB(OrientedBoundingBox obb, int color) {
        // Bottom rectangle (vertices 1, 2, 6, 5)
        GizmoDrawing.line(obb.vertex1, obb.vertex2, color);
        GizmoDrawing.line(obb.vertex2, obb.vertex6, color);
        GizmoDrawing.line(obb.vertex6, obb.vertex5, color);
        GizmoDrawing.line(obb.vertex5, obb.vertex1, color);

        // Top rectangle (vertices 4, 3, 7, 8)
        GizmoDrawing.line(obb.vertex4, obb.vertex3, color);
        GizmoDrawing.line(obb.vertex3, obb.vertex7, color);
        GizmoDrawing.line(obb.vertex7, obb.vertex8, color);
        GizmoDrawing.line(obb.vertex8, obb.vertex4, color);

        // Vertical edges connecting bottom to top
        GizmoDrawing.line(obb.vertex1, obb.vertex4, color);
        GizmoDrawing.line(obb.vertex2, obb.vertex3, color);
        GizmoDrawing.line(obb.vertex6, obb.vertex7, color);
        GizmoDrawing.line(obb.vertex5, obb.vertex8, color);
    }
}
