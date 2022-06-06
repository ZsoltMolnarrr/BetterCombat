package net.bettercombat.network;

import net.bettercombat.BetterCombat;
import net.bettercombat.ServerConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class Packets {
    public record C2S_AttackRequest(int comboCount, boolean isSneaking, int[] entityIds) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "c2s_request_attack");
        public static double RangeTolerance = 2.0;
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

    public record AttackAnimation(int playerId, boolean isOffHand, String animationName) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "attack_animation");
        public static String StopSymbol = "!STOP!";

        public static PacketByteBuf writeStop(int playerId) {
            return writePlay(playerId, false, StopSymbol);
        }

        public static PacketByteBuf writePlay(int playerId, boolean isOffHand, String animationName) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(playerId);
            buffer.writeBoolean(isOffHand);
            buffer.writeString(animationName);
            return buffer;
        }

        public static AttackAnimation read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            boolean isOffHand = buffer.readBoolean();
            String animationName = buffer.readString();
            return new AttackAnimation(playerId, isOffHand, animationName);
        }
    }

    public static class WeaponRegistrySync {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "weapon_registry");
    }

    public static class ConfigSync {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "config_sync");

        // TODO: Read/write whole object

        public static PacketByteBuf write(ServerConfig config) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(config.allow_fast_attacks);
            buffer.writeFloat(config.dual_wielding_attack_speed_multiplier);
            buffer.writeFloat(config.dual_wielding_main_hand_damage_multiplier);
            buffer.writeFloat(config.dual_wielding_off_hand_damage_multiplier);
            return buffer;
        }

        public static void readInPlace(PacketByteBuf buffer, ServerConfig config) {
            config.allow_fast_attacks = buffer.readBoolean();
            config.dual_wielding_attack_speed_multiplier = buffer.readFloat();
            config.dual_wielding_main_hand_damage_multiplier = buffer.readFloat();
            config.dual_wielding_off_hand_damage_multiplier = buffer.readFloat();
        }
    }
}
