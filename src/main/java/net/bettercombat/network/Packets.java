package net.bettercombat.network;

import me.lortseam.completeconfig.data.Config;
import net.bettercombat.BetterCombat;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class Packets {
    public record C2S_AttackRequest(int comboCount, boolean isSneaking, int[] entityIds) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "c2s_request_attack");
        public static boolean UseVanillaPacket = true;
        public static PacketByteBuf write(int comboCount, boolean isSneaking, List<Entity> entities) {
            PacketByteBuf buffer = PacketByteBufs.create();
            int[] ids = new int[entities.size()];
            for(int i = 0; i < entities.size(); i++) {
                ids[i] = entities.get(i).getId();
            }
            buffer.writeInt(comboCount);
            buffer.writeBoolean(isSneaking);
            buffer.writeIntArray(ids);
            return buffer;
        }

        public static C2S_AttackRequest read(PacketByteBuf buffer) {
            int comboCount = buffer.readInt();
            boolean isSneaking = buffer.readBoolean();
            int[] ids = buffer.readIntArray();
            return new C2S_AttackRequest(comboCount, isSneaking, ids);
        }
    }

    public record AttackAnimation(int playerId, boolean isOffHand, String animationName, float length) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "attack_animation");
        public static String StopSymbol = "!STOP!";

        public static PacketByteBuf writeStop(int playerId) {
            return writePlay(playerId, false, StopSymbol, 0);
        }

        public static PacketByteBuf writePlay(int playerId, boolean isOffHand, String animationName, float length) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(playerId);
            buffer.writeBoolean(isOffHand);
            buffer.writeString(animationName);
            buffer.writeFloat(length);
            return buffer;
        }

        public static AttackAnimation read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            boolean isOffHand = buffer.readBoolean();
            String animationName = buffer.readString();
            float length = buffer.readFloat();
            return new AttackAnimation(playerId, isOffHand, animationName, length);
        }
    }

    public record AttackSound(double x, double y, double z, String soundId, float volume, float pitch, long seed) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "attack_sound");
        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeDouble(x);
            buffer.writeDouble(y);
            buffer.writeDouble(z);
            buffer.writeString(soundId);
            buffer.writeFloat(volume);
            buffer.writeFloat(pitch);
            buffer.writeLong(seed);
            return buffer;
        }

        public static AttackSound read(PacketByteBuf buffer) {
            var x = buffer.readDouble();
            var y = buffer.readDouble();
            var z = buffer.readDouble();
            var soundId = buffer.readString();
            var volume = buffer.readFloat();
            var pitch = buffer.readFloat();
            var seed = buffer.readLong();
            return new AttackSound(x, y, z, soundId, volume, pitch, seed);
        }
    }

    public static class WeaponRegistrySync {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "weapon_registry");
    }

    public static class ConfigSync {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "config_sync");

        public static PacketByteBuf write(Config config) {
            var writer = new StringWriter();
            config.serialize(() -> new BufferedWriter(writer));
            var buffer = PacketByteBufs.create();
            buffer.writeString(writer.toString());
            return buffer;
        }

        public static void readInPlace(PacketByteBuf buffer, Config config) {
            config.deserialize(() -> new BufferedReader(new StringReader(buffer.readString())));
        }
    }
}
