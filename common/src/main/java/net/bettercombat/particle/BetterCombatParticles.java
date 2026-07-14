package net.bettercombat.particle;

import com.mojang.serialization.MapCodec;
import net.bettercombat.BetterCombatMod;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;

public class BetterCombatParticles {
    public record StaticParams(boolean animated, int maxAge, float scale) { }
    public record DynamicParams(float red, float green, float blue, float alpha) { }

//    private static class Helper extends SimpleParticleType {
//        protected Helper(boolean alwaysShow) {
//            super(alwaysShow);
//        }
//    }
//    private static SimpleParticleType createSimple() {
//        return new Helper(false);
//    }

    private static ParticleType<SlashParticleEffect> createParticle() {
        return new ParticleType<SlashParticleEffect>(false) {
            public MapCodec<SlashParticleEffect> codec() {
                return SlashParticleEffect.createCodec(this);
            }

            public StreamCodec<? super RegistryFriendlyByteBuf, SlashParticleEffect> streamCodec() {
                return SlashParticleEffect.createPacketCodec(this);
            }
        };
    }

    public record Texture(Identifier id, int frames) {
        public static Texture vanilla(String name) {
            return new Texture(Identifier.withDefaultNamespace(name), 1);
        }
        public static Texture vanilla(String name, int frames) {
            return new Texture(Identifier.withDefaultNamespace(name), frames);
        }
        public static Texture of(String name) {
            return new Texture(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, name), 1);
        }
        public static Texture of(String name, int frames) {
            return new Texture(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, name), frames);
        }
    }

    private static final int default_age = 20;
    private static final int default_frames = 8; // Ignored for now
    public record Entry(Identifier id, Texture texture, StaticParams params, ParticleType<SlashParticleEffect> particleType) {
        public Entry(String name, Texture texture, StaticParams params) {
            this(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, name), texture, params);
        }
        public Entry(String name, int textureFrames, StaticParams params) {
            this(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, name), Texture.of(name, textureFrames), params);
        }
        public Entry(Identifier id, Texture texture, StaticParams params) {
            this(id, texture, params, createParticle());
        }
        public Entry(String name) {
            this(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, name), Texture.of(name, default_frames), new StaticParams(true, default_age, 1F));
        }
    }

    public static final ArrayList<Entry> ENTRIES = new ArrayList<>();
    private static Entry add(Entry simpleEntry) {
        ENTRIES.add(simpleEntry);
        return simpleEntry;
    }

    public static final Entry botslash45 = add(new Entry("botslash45"));
    public static final Entry botslash90 = add(new Entry("botslash90"));
    public static final Entry botslash180 = add(new Entry("botslash180"));
    public static final Entry botslash270 = add(new Entry("botslash270"));
    public static final Entry botslash360 = add(new Entry("botslash360"));
    public static final Entry botstab = add(new Entry("botstab"));

    public static final Entry topslash45 = add(new Entry("topslash45"));
    public static final Entry topslash90 = add(new Entry("topslash90"));
    public static final Entry topslash180 = add(new Entry("topslash180"));
    public static final Entry topslash270 = add(new Entry("topslash270"));
    public static final Entry topslash360 = add(new Entry("topslash360"));
    public static final Entry topstab = add(new Entry("topstab"));

    public static void register() {
        for (var entry : ENTRIES) {
            Registry.register(BuiltInRegistries.PARTICLE_TYPE, entry.id, entry.particleType);
        }
    }
}
