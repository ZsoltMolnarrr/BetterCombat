package net.bettercombat.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bettercombat.client.OrientedBoundingBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class ColliderDebugRenderer {

    @Inject(method = "render",at = @At("TAIL"))
    public void renderColliderDebug(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }

        Vec3d cameraPosition = camera.getPos().negate();
        Vec3d center = player.getEyePos().add(cameraPosition);

        OrientedBoundingBox obb = new OrientedBoundingBox(center, 2, 1, 3,
                player.getPitch(), player.getYaw());


        drawLines(obb);

        if (client.options.attackKey.isPressed()) {
            System.out.println("yaw: " + player.getHeadYaw() + " pitch: " + player.getPitch());
             obb.printDebug();
        }
    }

    private void drawBox(OrientedBoundingBox obb) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        drawOBB(obb, bufferBuilder, 1, 1, 1, 0.1F);
        tessellator.draw();
    }

    private void drawOBB(OrientedBoundingBox box, BufferBuilder buffer, float red, float green, float blue, float alpha) {
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex8.x, box.vertex8.y, box.vertex8.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex8.x, box.vertex8.y, box.vertex8.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex2.x, box.vertex2.y, box.vertex2.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex2.x, box.vertex2.y, box.vertex2.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex2.x, box.vertex2.y, box.vertex2.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex6.x, box.vertex6.y, box.vertex6.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex6.x, box.vertex6.y, box.vertex6.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex8.x, box.vertex8.y, box.vertex8.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red, green, blue, alpha).next();
    }

    private void drawLines(OrientedBoundingBox obb) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        outlineOBB(obb, bufferBuilder, 1, 1, 1, 0.1F);
        look(obb, bufferBuilder, 1, 1, 1, 0.1F);

        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    private void outlineOBB(OrientedBoundingBox box, BufferBuilder buffer, float red, float green, float blue, float alpha) {
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex2.x, box.vertex2.y, box.vertex2.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex3.x, box.vertex3.y, box.vertex3.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex4.x, box.vertex4.y, box.vertex4.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex1.x, box.vertex1.y, box.vertex1.z).color(red, green, blue, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red, 0, 0, alpha).next();
        buffer.vertex(box.vertex6.x, box.vertex6.y, box.vertex6.z).color(red, 0, 0, alpha).next();
        buffer.vertex(box.vertex7.x, box.vertex7.y, box.vertex7.z).color(red, 0, 0, alpha).next();
        buffer.vertex(box.vertex8.x, box.vertex8.y, box.vertex8.z).color(red, 0, 0, alpha).next();
        buffer.vertex(box.vertex5.x, box.vertex5.y, box.vertex5.z).color(red, 0, 0, alpha).next();
    }

    private void look(OrientedBoundingBox box, BufferBuilder buffer, float red, float green, float blue, float alpha) {

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(1, 0, blue, 0).next();
        buffer.vertex(box.orientation_u.x, box.orientation_u.y, box.orientation_u.z).color(1, 0, blue, 0).next();

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 1, 0, alpha).next();
        buffer.vertex(box.orientation_v.x, box.orientation_v.y, box.orientation_v.z).color(0, 1, 0, alpha).next();

        buffer.vertex(box.center.x, box.center.y, box.center.z).color(0, 0, 1, alpha).next();
        buffer.vertex(box.orientation_w.x, box.orientation_w.y, box.orientation_w.z).color(0, 0, 1, alpha).next();
    }
}
