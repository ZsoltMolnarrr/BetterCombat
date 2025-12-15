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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
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
        // super(world, x, y, z, 0.0, 0.0, 0.0);
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

    @Override
    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float x = (float)(this.lastX - cameraPos.getX());
        float y = (float)(this.lastY - cameraPos.getY());
        float z = (float)(this.lastZ - cameraPos.getZ());
        float size = this.getSize(tickDelta);
        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.identity();
        rotationMatrix.rotate((float)Math.toRadians((double)(-this.yaw)), new Vector3f(0.0F, 1.0F, 0.0F));
        rotationMatrix.rotate((float)Math.toRadians((double)this.pitch), new Vector3f(1.0F, 0.0F, 0.0F));
        rotationMatrix.rotate((float)Math.toRadians((double)this.roll), new Vector3f(0.0F, 0.0F, 1.0F));
        rotationMatrix.rotate((float)Math.toRadians((double)(-this.localYaw)), new Vector3f(0.0F, 1.0F, 0.0F));
        Vector4f[] corners = new Vector4f[]{new Vector4f(-size, this.modelOffset, -size, 1.0F), new Vector4f(-size, this.modelOffset, size, 1.0F), new Vector4f(size, this.modelOffset, size, 1.0F), new Vector4f(size, this.modelOffset, -size, 1.0F)};
        Vector4f[] var15 = corners;
        int var16 = corners.length;

        for(int var17 = 0; var17 < var16; ++var17) {
            Vector4f corner = var15[var17];
            rotationMatrix.transform(corner);
            corner.add(x, y, z, 0.0F);
        }

//        vertexConsumer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
//        vertexConsumer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta));
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