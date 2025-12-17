package net.bettercombat.fabric;

import net.bettercombat.Platform;
import net.bettercombat.fabric.attachment.FabricPlayerAttachments;
import net.bettercombat.client.compat.SpellEngineCompatibility;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

import static net.bettercombat.Platform.Type.FABRIC;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return FABRIC;
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean isCastingSpell(PlayerEntity player) {
        return SpellEngineCompatibility.isCastingSpell(player);
    }

    public static PacketByteBuf createByteBuffer() {
        return PacketByteBufs.create();
    }

    public static Collection<ServerPlayerEntity> tracking(ServerPlayerEntity player) {
        return PlayerLookup.tracking(player);
    }

    public static Collection<ServerPlayerEntity> around(ServerWorld world, Vec3d origin, double distance) {
        return PlayerLookup.around(world, origin, distance);
    }

    public static boolean networkS2C_CanSend(ServerPlayerEntity player, Identifier packetId) {
        return ServerPlayNetworking.canSend(player, packetId);
    }

    public static void networkS2C_Send(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void networkC2S_Send(CustomPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static class PlayerAttachmentsImpl implements Platform.PlayerAttachments {
        @Override
        public String getMainHandIdleAnimation(PlayerEntity player) {
            return FabricPlayerAttachments.getMainHandIdleAnimation(player);
        }

        @Override
        public String getOffHandIdleAnimation(PlayerEntity player) {
            return FabricPlayerAttachments.getOffHandIdleAnimation(player);
        }

        @Override
        public void setMainHandIdleAnimation(PlayerEntity player, String animation) {
            FabricPlayerAttachments.setMainHandIdleAnimation(player, animation);
        }

        @Override
        public void setOffHandIdleAnimation(PlayerEntity player, String animation) {
            FabricPlayerAttachments.setOffHandIdleAnimation(player, animation);
        }
    }
    private static final PlayerAttachmentsImpl attachments = new PlayerAttachmentsImpl();
    public static Platform.PlayerAttachments playerAttachments() {
        return attachments;
    }
}
