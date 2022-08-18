package net.bettercombat.helper.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ClientPlayNetworking {
    public static void registerGlobalReceiver(Identifier id, PlayChannelHandler handler){

    }

    public static boolean canSend(PlayerEntity serverPlayer, Identifier id) {
        return serverPlayer != null;
    }

    public static void send(Identifier id, PacketByteBuf forwardBuffer) {

    }

    public interface PlayChannelHandler {
        void receive(MinecraftClient server, ClientPlayNetworkHandler handler, PacketByteBuf buf, Object responseSender);
    }
}
