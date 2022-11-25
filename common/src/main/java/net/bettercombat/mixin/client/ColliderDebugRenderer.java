package net.bettercombat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.collision.OrientedBoundingBox;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(DebugRenderer.class)
public class ColliderDebugRenderer {
    @Inject(method = "render",at = @At("TAIL"))
    public void renderColliderDebug(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!((MinecraftClientAccessor) client).getEntityRenderDispatcher().shouldRenderHitboxes()) {
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (!BetterCombatClient.config.isDebugOBBEnabled) {
            return;
        }
        Camera camera = client.gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        if (client.player.getMainHandStack() == null) {
            return;
        }
        var extendedClient = (MinecraftClient_BetterCombat)client;
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
        var target = TargetFinder.findAttackTargetResult(
                player,
                cursorTarget,
                hand.attack(),
                attributes.attackRange());
        boolean collides = target.entities.size() > 0;
        Vec3d cameraOffset = camera.getPos().negate();
        var obb = target.obb.
                copy()
                .offset(cameraOffset)
                .updateVertex();
        List<OrientedBoundingBox> collidingObbs = target.entities.stream()
                .map(entity -> new OrientedBoundingBox(entity.getBoundingBox())
                        .offset(cameraOffset)
                        .scale(0.95)
                        .updateVertex())
                .collect(Collectors.toList());
        drawOutline(obb, collidingObbs, collides);
    }

    private void drawOutline(OrientedBoundingBox obb, List<OrientedBoundingBox> otherObbs, boolean collides) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        if (collides) {
            outlineOBB(obb, bufferBuilder,
                    1, 0, 0,
                    1, 0, 0,0.5F);
        } else {
            outlineOBB(obb, bufferBuilder,
                    0, 1, 0,
                    1, 1, 0,0.5F);
        }
        look(obb, bufferBuilder, 0.5F);

        for(OrientedBoundingBox otherObb: otherObbs){
            outlineOBB(otherObb, bufferBuilder,
                    1, 0, 0,
                    1, 0, 0,0.5F);
        }

        tessellator.draw();

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    private void outlineOBB(OrientedBoundingBox box, BufferBuilder buffer,
                            float red1, float green1, float blue1,
                            float red2, float green2, float blue2,
                            float alpha) {
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(0, 0, 0, 0).next();

        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex2.x, box.vertex2.y, box.vertex2.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex6.x, box.vertex6.y, box.vertex6.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex2.x, box.vertex2.y, box.vertex2.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex6.x, box.vertex6.y, box.vertex6.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex8.x, box.vertex8.y, box.vertex8.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red1, green1, blue1, alpha).next();
        buffer.vertex(box.vertex8.x, box.vertex8.y, box.vertex8.z).color(red2, green2, blue2, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red2, green2, blue2, alpha).next();

        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(0, 0, 0, 0).next();
        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 0, 0, 0).next();
    }

    private void look(OrientedBoundingBox box, BufferBuilder buffer, float alpha) {
        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 0, 0, alpha).next();

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(1, 0, 0, alpha).next();
        buffer.vertex(box.axisZ.x, box.axisZ.y, box.axisZ.z).color(1, 0, 0, alpha).next();
        buffer.vertex(box.center.x, box.center.y, box.center.z).color(1, 0, 0, alpha).next();

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 1, 0, alpha).next();
        buffer.vertex(box.axisY.x, box.axisY.y, box.axisY.z).color(0, 1, 0, alpha).next();
        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 1, 0, alpha).next();

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 0, 1, alpha).next();
        buffer.vertex(box.axisX.x, box.axisX.y, box.axisX.z).color(0, 0, 1, alpha).next();
        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 0, 1, alpha).next();

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 0, 0, alpha).next();
    }

    public void printDebug(OrientedBoundingBox obb) {
        Vec3d extent_x = obb.axisX.multiply(obb.extent.x);
        Vec3d extent_y = obb.axisY.multiply(obb.extent.y);
        Vec3d extent_z = obb.axisZ.multiply(obb.extent.z);
        System.out.println("Center: " + vec3Short(obb.center) + " Extent: " + vec3Short(obb.extent) );
        System.out.println("scaledAxisX: " + vec3Short(obb.scaledAxisX)
                + "scaledAxisY: " + vec3Short(obb.scaledAxisY)
                + "scaledAxisZ: " + vec3Short(obb.scaledAxisZ));
        System.out.println("1:" + vec3Short(obb.vertex1)
                + " 2:" + vec3Short(obb.vertex2)
                + " 3:" + vec3Short(obb.vertex3)
                + " 4:" + vec3Short(obb.vertex4));
        System.out.println("5:" + vec3Short(obb.vertex5)
                + " 6:" + vec3Short(obb.vertex6)
                + " 7:" + vec3Short(obb.vertex7)
                + " 8:" + vec3Short(obb.vertex8));
    }

    private String vec3Short(Vec3d vec) {
        return "{" + String.format("%.3f", vec.x) + ", "  + String.format("%.3f", vec.y) + ", "  + String.format("%.3f", vec.z) + "}";
    }

    private Vec3d[] getVertices(Box box) {
        return new Vec3d[]{
            new Vec3d(box.minX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.minZ),
            new Vec3d(box.minX, box.maxY, box.minZ),
            new Vec3d(box.minX, box.minY, box.maxZ),
            new Vec3d(box.maxX, box.maxY, box.minZ),
            new Vec3d(box.minX, box.maxY, box.maxZ),
            new Vec3d(box.maxX, box.minY, box.maxZ),
            new Vec3d(box.maxX, box.maxY, box.maxZ)
        };
    }
}
