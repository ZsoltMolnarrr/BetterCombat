package net.bettercombat.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class SlashParticleEffect implements ParticleEffect {
    private final ParticleType<SlashParticleEffect> type;
    private final float pitch;
    private final float yaw;
    private final float localYaw;
    private final float roll;
    private final float scale;
    private final boolean light;
    private final long color_rgba;

    public SlashParticleEffect(ParticleType<SlashParticleEffect> type, float scale, float pitch, float yaw, float localYaw, float roll, boolean light, long color_rgba) {
        this.type = type;
        this.scale = scale;
        this.pitch = pitch;
        this.yaw = yaw;
        this.localYaw = localYaw;
        this.roll = roll;
        this.light = light;
        this.color_rgba = color_rgba;
    }

    public ParticleType<SlashParticleEffect> getType() {
        return this.type;
    }

    public float getScale() {
        return this.scale;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getLocalYaw() {
        return this.localYaw;
    }

    public float getRoll() {
        return this.roll;
    }

    public boolean getLight() {
        return this.light;
    }

    public long getColorRGBA() {
        return this.color_rgba;
    }

    public static MapCodec<SlashParticleEffect> createCodec(ParticleType<SlashParticleEffect> particleType) {
        return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(
                    Codec.FLOAT.fieldOf("scale").forGetter(SlashParticleEffect::getScale),
                    Codec.FLOAT.fieldOf("pitch").forGetter(SlashParticleEffect::getPitch),
                    Codec.FLOAT.fieldOf("yaw").forGetter(SlashParticleEffect::getYaw),
                    Codec.FLOAT.fieldOf("local_yaw").forGetter(SlashParticleEffect::getLocalYaw),
                    Codec.FLOAT.fieldOf("roll").forGetter(SlashParticleEffect::getRoll),
                    Codec.BOOL.fieldOf("light").forGetter(SlashParticleEffect::getLight),
                    Codec.LONG.fieldOf("color_rgba").forGetter(SlashParticleEffect::getColorRGBA)
            ).apply(instance, (scale, pitch, yaw, localYaw, roll, light, color_rgba) -> {
                return new SlashParticleEffect(particleType, scale, pitch, yaw, localYaw, roll, light, color_rgba);
            });
        });
    }

    public static PacketCodec<? super RegistryByteBuf, SlashParticleEffect> createPacketCodec(final ParticleType<SlashParticleEffect> particleType) {
        return new PacketCodec<RegistryByteBuf, SlashParticleEffect>() {
            public SlashParticleEffect decode(RegistryByteBuf buf) {
                float scale = buf.readFloat();
                float pitch = buf.readFloat();
                float yaw = buf.readFloat();
                float localYaw = buf.readFloat();
                float roll = buf.readFloat();
                boolean light = buf.readBoolean();
                long color_rgba = buf.readLong();
                return new SlashParticleEffect(particleType, scale, pitch, yaw, localYaw, roll, light, color_rgba);
            }

            public void encode(RegistryByteBuf buf, SlashParticleEffect effect) {
                buf.writeFloat(effect.getScale());
                buf.writeFloat(effect.getPitch());
                buf.writeFloat(effect.getYaw());
                buf.writeFloat(effect.getLocalYaw());
                buf.writeFloat(effect.getRoll());
                buf.writeBoolean(effect.getLight());
                buf.writeLong(effect.getColorRGBA());
            }
        };
    }
}
