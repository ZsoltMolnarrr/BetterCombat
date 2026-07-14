package net.bettercombat.neoforge;

import io.netty.buffer.Unpooled;
import net.bettercombat.Platform;
import net.bettercombat.neoforge.attachment.NeoForgePlayerAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.bettercombat.client.compat.SpellEngineCompatibility;

import java.util.Collection;

import static net.bettercombat.Platform.Type.NEOFORGE;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return NEOFORGE;
    }

    public static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    public static boolean isCastingSpell(Player player) {
        return SpellEngineCompatibility.isCastingSpell(player);
    }

    public static Collection<ServerPlayer> tracking(ServerPlayer player) {
        return (Collection<ServerPlayer>) player.level().players();
    }

    public static FriendlyByteBuf createByteBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static Collection<ServerPlayer> around(ServerLevel world, Vec3 origin, double distance) {
        return world.getPlayers((player) -> player.position().distanceToSqr(origin) <= (distance*distance));
    }

    public static boolean networkS2C_CanSend(ServerPlayer player, Identifier packetId) {
        return true;
    }

    public static void networkS2C_Send(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void networkC2S_Send(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    public static class PlayerAttachmentsImpl implements Platform.PlayerAttachments {
        @Override
        public String getMainHandIdleAnimation(Player player) {
            return NeoForgePlayerAttachments.getMainHandIdleAnimation(player);
        }

        @Override
        public String getOffHandIdleAnimation(Player player) {
            return NeoForgePlayerAttachments.getOffHandIdleAnimation(player);
        }

        @Override
        public void setMainHandIdleAnimation(Player player, String animation) {
            NeoForgePlayerAttachments.setMainHandIdleAnimation(player, animation);
        }

        @Override
        public void setOffHandIdleAnimation(Player player, String animation) {
            NeoForgePlayerAttachments.setOffHandIdleAnimation(player, animation);
        }

        @Override
        public byte getCombatFlags(Player player) {
            return NeoForgePlayerAttachments.getCombatFlags(player);
        }

        @Override
        public void setCombatFlags(Player player, byte flags) {
            NeoForgePlayerAttachments.setCombatFlags(player, flags);
        }
    }
    private static final PlayerAttachmentsImpl attachments = new PlayerAttachmentsImpl();
    public static Platform.PlayerAttachments playerAttachments() {
        return attachments;
    }
}
