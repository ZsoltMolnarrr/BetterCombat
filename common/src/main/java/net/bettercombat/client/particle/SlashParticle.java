package net.bettercombat.client.particle;

import net.bettercombat.api.fx.Color;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SlashParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;
    public final float modelOffset;
    private final float pitch;
    private final float yaw;
    private final float localYaw;
    private final float roll;
    private final boolean light;

    public SlashParticle(ClientLevel world, double x, double y, double z, float scale, float pitch, float yaw, float localYaw, float roll, boolean light, long color_rgba, SpriteSet spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.first());
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

        this.lifetime = 6;
        this.modelOffset = this.setModelOffset();
        this.quadSize = scale;
        this.setSpriteFromAge(spriteProvider);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.spriteProvider);
        }
    }

    protected int getLightCoords(float tint) {
        BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
        if (this.light) {
            return 15728880;
        } else {
            return this.level.hasChunkAt(blockPos) ? LightCoordsUtil.getLightCoords(this.level, blockPos) : 0;
        }
    }

    public Particle scale(float scale) {
        this.quadSize = scale;
        return super.scale(scale);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public float setModelOffset() {
        return 0.0F;
    }

    public void extract(QuadParticleRenderState submittable, Camera camera, float tickDelta) {
        Vec3 cameraPos = camera.position();

        float x = (float)(this.xo - cameraPos.x());
        float y = (float)(this.yo - cameraPos.y());
        float z = (float)(this.zo - cameraPos.z());

        float size = this.getQuadSize(tickDelta);

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
        float minU = this.getU0();
        float maxU = this.getU1();
        float minV = this.getV0();
        float maxV = this.getV1();

        // Apply vertical offset, add extra offset here if this method as helper
        y += (this.modelOffset);

        int color = ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol);
        int brightness = this.getLightCoords(tickDelta);

        // Front face render
        submittable.add(this.getLayer(), x, y, z, rotationX, rotationY, rotationZ, rotationW, size, maxU, minU, minV, maxV, color, brightness);

        // For the back face, we need to rotate 180 degrees around Y
        Quaternionf backRotation = new Quaternionf().rotateY((float)Math.PI);
        rotation.mul(backRotation);
        // Extract updated quaternion components
        rotationX = rotation.x;
        rotationY = rotation.y;
        rotationZ = rotation.z;
        rotationW = rotation.w;

        // Back face render after rotation
        submittable.add(this.getLayer(),x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness);
    }

    public static class Provider implements ParticleProvider<SlashParticleEffect> {
        private final SpriteSet spriteProvider;
        private final BetterCombatParticles.StaticParams params;

        public Provider(SpriteSet spriteProvider, BetterCombatParticles.StaticParams params) {
            this.spriteProvider = spriteProvider;
            this.params = params;
        }

        @Override
        public @Nullable Particle createParticle(SlashParticleEffect settings, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random) {
            return new SlashParticle(clientWorld, x, y, z, settings.getScale(), settings.getPitch(), settings.getYaw(), settings.getLocalYaw(), settings.getRoll(), settings.getLight(), settings.getColorRGBA(), this.spriteProvider);
        }
    }
}