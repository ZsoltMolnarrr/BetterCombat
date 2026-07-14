package net.bettercombat.network;

import com.google.gson.Gson;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.logic.AnimatedHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Packets {
    public record C2S_AttackRequest(int comboCount, boolean isSneaking, int selectedSlot, int cursorTarget, int[] entityIds) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "c2s_request_attack");
        public static final CustomPacketPayload.Type<C2S_AttackRequest> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, C2S_AttackRequest> CODEC = StreamCodec.ofMember(C2S_AttackRequest::write, C2S_AttackRequest::read);

        public C2S_AttackRequest(int comboCount, boolean isSneaking, int selectedSlot, @Nullable Entity cursorTarget, List<Entity> entities) {
            this(comboCount, isSneaking, selectedSlot, convertEntity(cursorTarget), convertEntityList(entities));
        }

        private static int[] convertEntityList(List<Entity> entities) {
            int[] ids = new int[entities.size()];
            for(int i = 0; i < entities.size(); i++) {
                var entity = entities.get(i);
                ids[i] = entity.getId();
            }
            return ids;
        }
        private static int convertEntity(@Nullable Entity entity) {
            if (entity == null) { return -1; }
            return entity.getId();
        }

        public static boolean UseVanillaPacket = true;
        public void write(FriendlyByteBuf buffer) {
            buffer.writeInt(comboCount);
            buffer.writeBoolean(isSneaking);
            buffer.writeInt(selectedSlot);
            buffer.writeInt(cursorTarget);
            buffer.writeVarIntArray(entityIds);
        }

        public static C2S_AttackRequest read(FriendlyByteBuf buffer) {
            int comboCount = buffer.readInt();
            boolean isSneaking = buffer.readBoolean();
            int selectedSlot = buffer.readInt();
            int cursorTarget = buffer.readInt();
            int[] ids = buffer.readVarIntArray();
            return new C2S_AttackRequest(comboCount, isSneaking, selectedSlot, cursorTarget, ids);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public record SwingParticles(List<ParticlePlacement> particles, TrailAppearance appearance) {
        public static final SwingParticles EMPTY = new SwingParticles(List.of(), new TrailAppearance());
    }
    public record AttackAnimation(int playerId, AnimatedHand animatedHand, String animationName, float length, float upswing, float weaponRange, int upswingTicks, SwingParticles particles) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "attack_animation");
        public static final CustomPacketPayload.Type<AttackAnimation> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, AttackAnimation> CODEC = StreamCodec.ofMember(AttackAnimation::write, AttackAnimation::read);

        private static final Gson gson = new Gson();
        public static String StopSymbol = "!STOP!";
        public static AttackAnimation stop(int playerId, int length) { return new AttackAnimation(playerId, AnimatedHand.MAIN_HAND, StopSymbol, length, 0, 0, 0, SwingParticles.EMPTY); }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeInt(playerId);
            buffer.writeInt(animatedHand.ordinal());
            buffer.writeUtf(animationName);
            buffer.writeFloat(length);
            buffer.writeFloat(upswing);
            buffer.writeFloat(weaponRange);
            buffer.writeInt(upswingTicks);
            // Write list of particles
            buffer.writeUtf(gson.toJson(particles));
        }

        public static AttackAnimation read(FriendlyByteBuf buffer) {
            int playerId = buffer.readInt();
            var animatedHand = AnimatedHand.values()[buffer.readInt()];
            String animationName = buffer.readUtf();
            float length = buffer.readFloat();
            float upswing = buffer.readFloat();
            float weaponRange = buffer.readFloat();
            int upswingTicks = buffer.readInt();
            var json = buffer.readUtf();
            var particles = gson.fromJson(json, SwingParticles.class);
            return new AttackAnimation(playerId, animatedHand, animationName, length, upswing, weaponRange, upswingTicks, particles);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public record AttackSound(double x, double y, double z, String soundId, float volume, float pitch, long seed) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "attack_sound");
        public static final CustomPacketPayload.Type<AttackSound> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, AttackSound> CODEC = StreamCodec.ofMember(AttackSound::write, AttackSound::read);

        public void write(FriendlyByteBuf buffer) {
            buffer.writeDouble(x);
            buffer.writeDouble(y);
            buffer.writeDouble(z);
            buffer.writeUtf(soundId);
            buffer.writeFloat(volume);
            buffer.writeFloat(pitch);
            buffer.writeLong(seed);
        }

        public static AttackSound read(FriendlyByteBuf buffer) {
            var x = buffer.readDouble();
            var y = buffer.readDouble();
            var z = buffer.readDouble();
            var soundId = buffer.readUtf();
            var volume = buffer.readFloat();
            var pitch = buffer.readFloat();
            var seed = buffer.readLong();
            return new AttackSound(x, y, z, soundId, volume, pitch, seed);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public record WeaponRegistrySync(boolean compressed, List<String> chunks) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "weapon_registry");
        public static final CustomPacketPayload.Type<WeaponRegistrySync> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<FriendlyByteBuf, WeaponRegistrySync> CODEC = StreamCodec.ofMember(WeaponRegistrySync::write, WeaponRegistrySync::read);

        public void write(FriendlyByteBuf buffer) {
            buffer.writeBoolean(compressed);
            buffer.writeInt(chunks.size());
            for (var chunk: chunks) {
                buffer.writeUtf(chunk);
            }
        }

        public static WeaponRegistrySync read(FriendlyByteBuf buffer) {
            var compressed = buffer.readBoolean();
            var chunkCount = buffer.readInt();
            var chunks = new ArrayList<String>();
            for (int i = 0; i < chunkCount; ++i) {
                chunks.add(buffer.readUtf());
            }
            return new WeaponRegistrySync(compressed, chunks);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public record C2S_BlockHit(BlockPos pos) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "block_hit");
        public static final CustomPacketPayload.Type<C2S_BlockHit> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<FriendlyByteBuf, C2S_BlockHit> CODEC = BlockPos.STREAM_CODEC.map(C2S_BlockHit::new, C2S_BlockHit::pos).cast();

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public record ConfigSync(String json) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "config_sync");
        public static final CustomPacketPayload.Type<ConfigSync> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<FriendlyByteBuf, ConfigSync> CODEC = StreamCodec.ofMember(ConfigSync::write, ConfigSync::read);

        private static final Gson gson = new Gson();
        public static String serialize(ServerConfig config) {
            return gson.toJson(config);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeUtf(json);
        }

        public static ConfigSync read(FriendlyByteBuf buffer) {
            var json = buffer.readUtf();
            return new ConfigSync(json);
        }

        public ServerConfig deserialized() {
            return gson.fromJson(json, ServerConfig.class);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public record Ack(String code) implements CustomPacketPayload {
        public static Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "ack");
        public static final CustomPacketPayload.Type<Ack> PACKET_ID = new CustomPacketPayload.Type<>(ID);
        public static final StreamCodec<FriendlyByteBuf, Ack> CODEC = StreamCodec.ofMember(Ack::write, Ack::read);

        public void write(FriendlyByteBuf buffer) {
            buffer.writeUtf(code);
        }

        public static Ack read(FriendlyByteBuf buffer) {
            var code = buffer.readUtf();
            return new Ack(code);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }
}