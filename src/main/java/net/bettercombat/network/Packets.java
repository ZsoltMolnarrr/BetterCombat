package net.bettercombat.network;

import net.bettercombat.BetterCombat;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class Packets {
    public record C2S_AttackRequest(int comboCount, int stack, boolean isSneaking, int[] entityIds) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "c2s_request_attack");
        public static double RangeTolerance = 2.0;
        public static boolean UseVanillaPacket = true;
        public static PacketByteBuf write(PacketByteBuf buffer, int comboCount, boolean useMainHand, boolean isSneaking, List<Entity> entities) {
            int[] ids = new int[entities.size()];
            for(int i = 0; i < entities.size(); i++) {
                ids[i] = entities.get(i).getId();
            }
            buffer.writeInt(comboCount);
            buffer.writeInt(useMainHand ? 0 : 1);
            buffer.writeBoolean(isSneaking);
            buffer.writeIntArray(ids);
            return buffer;
        }

        public static C2S_AttackRequest read(PacketByteBuf buffer) {
            int comboCount = buffer.readInt();
            int stack = buffer.readInt();
            boolean isSneaking = buffer.readBoolean();
            int[] ids = buffer.readIntArray();
            return new C2S_AttackRequest(comboCount, stack, isSneaking, ids);
        }
    }

    public record AttackAnimation(int playerId, String animationName) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "attack_animation");
        public static String StopSymbol = "STOP";

        public static PacketByteBuf writeStop(PacketByteBuf buffer, int playerId) {
            return writePlay(buffer, playerId, StopSymbol);
        }

        public static PacketByteBuf writePlay(PacketByteBuf buffer, int playerId, String animationName) {
            buffer.writeInt(playerId);
            buffer.writeString(animationName);
            return buffer;
        }

        public static AttackAnimation read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            String animationName = buffer.readString();
            return new AttackAnimation(playerId, animationName);
        }
    }
}
