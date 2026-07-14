package net.bettercombat.fabric;

import net.bettercombat.Platform;
import net.bettercombat.fabric.attachment.FabricPlayerAttachments;
import net.bettercombat.client.compat.SpellEngineCompatibility;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.Collection;

import static net.bettercombat.Platform.Type.FABRIC;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return FABRIC;
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean isCastingSpell(Player player) {
        return SpellEngineCompatibility.isCastingSpell(player);
    }

    public static FriendlyByteBuf createByteBuffer() {
        return PacketByteBufs.create();
    }

    public static Collection<ServerPlayer> tracking(ServerPlayer player) {
        return PlayerLookup.tracking(player);
    }

    public static Collection<ServerPlayer> around(ServerLevel world, Vec3 origin, double distance) {
        return PlayerLookup.around(world, origin, distance);
    }

    public static boolean networkS2C_CanSend(ServerPlayer player, Identifier packetId) {
        return ServerPlayNetworking.canSend(player, packetId);
    }

    public static void networkS2C_Send(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void networkC2S_Send(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static class PlayerAttachmentsImpl implements Platform.PlayerAttachments {
        @Override
        public String getMainHandIdleAnimation(Player player) {
            return FabricPlayerAttachments.getMainHandIdleAnimation(player);
        }

        @Override
        public String getOffHandIdleAnimation(Player player) {
            return FabricPlayerAttachments.getOffHandIdleAnimation(player);
        }

        @Override
        public void setMainHandIdleAnimation(Player player, String animation) {
            FabricPlayerAttachments.setMainHandIdleAnimation(player, animation);
        }

        @Override
        public void setOffHandIdleAnimation(Player player, String animation) {
            FabricPlayerAttachments.setOffHandIdleAnimation(player, animation);
        }

        @Override
        public byte getCombatFlags(Player player) {
            return FabricPlayerAttachments.getCombatFlags(player);
        }

        @Override
        public void setCombatFlags(Player player, byte flags) {
            FabricPlayerAttachments.setCombatFlags(player, flags);
        }
    }
    private static final PlayerAttachmentsImpl attachments = new PlayerAttachmentsImpl();
    public static Platform.PlayerAttachments playerAttachments() {
        return attachments;
    }
}
