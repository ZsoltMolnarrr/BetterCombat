package net.bettercombat.client.particle;

import net.bettercombat.api.fx.Color;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.particle.SlashParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class SlashParticle extends BillboardParticle {
    private final SpriteProvider spriteProvider;
    public final float modelOffset;
    private final float pitch;
    private final float yaw;
    private final float localYaw;
    private final float roll;
    private final boolean light;

    public SlashParticle(ClientWorld world, double x, double y, double z, float scale, float pitch, float yaw, float localYaw, float roll, boolean light, long color_rgba, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.light = light;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.localYaw = localYaw;

        // Set color from RGBA long
        var color = Color.fromRGBA(color_rgba);
        this.setColor(color.red(), color.green(), color.blue());
        this.alpha = color.alpha();

        this.maxAge = 6;
        this.modelOffset = this.setModelOffset();
        this.scale = scale;
        this.updateSprite(spriteProvider);
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.updateSprite(this.spriteProvider);
        }
    }

    protected int getBrightness(float tint) {
        BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
        if (this.light) {
            return 15728880;
        } else {
            return this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
        }
    }

    public Particle scale(float scale) {
        this.scale = scale;
        return super.scale(scale);
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public float setModelOffset() {
        return 0.0F;
    }

    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getCameraPos();

        float x = (float)(this.lastX - cameraPos.getX());
        float y = (float)(this.lastY - cameraPos.getY());
        float z = (float)(this.lastZ - cameraPos.getZ());

        float size = this.getSize(tickDelta);

        // Create quaternion rotation
        Quaternionf rotation = new Quaternionf();

        // Readjust rotation here to match the particle data or config
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.identity();
        rotationMatrix.rotateY((float)Math.toRadians(-this.yaw));
        rotationMatrix.rotateX((float)Math.toRadians(this.pitch+90));
        rotationMatrix.rotateY((float)Math.toRadians(this.roll));
        rotationMatrix.rotateZ((float)Math.toRadians(this.localYaw));

        // Convert matrix to quaternion
        rotation.setFromNormalized(rotationMatrix);

        // Extract quaternion components for the render method
        float rotationX = rotation.x;
        float rotationY = rotation.y;
        float rotationZ = rotation.z;
        float rotationW = rotation.w;

        // Calculate UV coordinates
        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();

        // Apply vertical offset, add extra offset here if this method as helper
        y += (this.modelOffset);

        int color = ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue);
        int brightness = this.getBrightness(tickDelta);

        // Front face render
        submittable.render(this.getRenderType(), x, y, z, rotationX, rotationY, rotationZ, rotationW, size, maxU, minU, minV, maxV, color, brightness);

        // For the back face, we need to rotate 180 degrees around Y
        Quaternionf backRotation = new Quaternionf().rotateY((float)Math.PI);
        rotation.mul(backRotation);
        // Extract updated quaternion components
        rotationX = rotation.x;
        rotationY = rotation.y;
        rotationZ = rotation.z;
        rotationW = rotation.w;

        // Back face render after rotation
        submittable.render(this.getRenderType(),x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness);
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleFactory<SlashParticleEffect> {
        private final SpriteProvider spriteProvider;
        private final BetterCombatParticles.StaticParams params;

        public Provider(SpriteProvider spriteProvider, BetterCombatParticles.StaticParams params) {
            this.spriteProvider = spriteProvider;
            this.params = params;
        }

        @Override
        public @Nullable Particle createParticle(SlashParticleEffect settings, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
            return new SlashParticle(clientWorld, x, y, z, settings.getScale(), settings.getPitch(), settings.getYaw(), settings.getLocalYaw(), settings.getRoll(), settings.getLight(), settings.getColorRGBA(), this.spriteProvider);
        }
    }
}