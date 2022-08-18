package net.fabricmc.fabric.api.client.networking.v1;

import net.bettercombat.helper.network.forge.NetworkHandler;
import net.bettercombat.helper.network.forge.PacketWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class ServerPlayNetworking {
    public static Map<Identifier, PlayChannelHandler> HANDLERS = new HashMap<>();

    public static void registerGlobalReceiver(Identifier id, PlayChannelHandler handler){
        HANDLERS.put(id, handler);
    }

    public static boolean canSend(PlayerEntity serverPlayer, Identifier id) {
        return serverPlayer != null;
    }

    public static void send(PlayerEntity serverPlayer, Identifier id, PacketByteBuf forwardBuffer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) serverPlayer), new PacketWrapper(true, id, forwardBuffer));
    }

    public static void handle(PacketWrapper msg, ServerPlayerEntity sender) {
        HANDLERS.get(msg.packetType).receive(sender.getServer(), sender, sender.networkHandler, new PacketByteBuf(msg.data), null);
    }

    public interface PlayChannelHandler {
        void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, Object responseSender);
    }
}
