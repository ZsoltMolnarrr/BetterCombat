package net.bettercombat.helper.network;

import net.bettercombat.helper.network.forge.NetworkHandler;
import net.bettercombat.helper.network.forge.PacketWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class ClientPlayNetworking {
    public static Map<Identifier, PlayChannelHandler> HANDLERS = new HashMap<>();

    public static void registerGlobalReceiver(Identifier id, PlayChannelHandler handler){
        HANDLERS.put(id, handler);
    }

    public static void send(Identifier id, PacketByteBuf forwardBuffer) {
        NetworkHandler.INSTANCE.sendToServer(new PacketWrapper(false, id, forwardBuffer));
    }

    public static void handle(PacketWrapper msg) {
        HANDLERS.get(msg.packetType).receive(MinecraftClient.getInstance(), MinecraftClient.getInstance().getNetworkHandler(), new PacketByteBuf(msg.data), null);
    }

    public interface PlayChannelHandler {
        void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, Object responseSender);
    }
}
