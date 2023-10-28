package net.fabricmc.fabric.api.client.networking.v1;

import net.bettercombat.forge.network.NetworkHandler;
import net.bettercombat.forge.network.PacketWrapper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ClientPlayNetworking {
    public static Map<Identifier, PlayChannelHandler> HANDLERS = new HashMap<>();

    public static boolean registerGlobalReceiver(Identifier id, PlayChannelHandler handler){
        HANDLERS.put(id, handler);
        return true;
    }

    public static void send(Identifier id, PacketByteBuf forwardBuffer) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler != null) {
            NetworkHandler.INSTANCE.send(new PacketWrapper(false, id, forwardBuffer), networkHandler.getConnection());
        }
    }

    public static void handle(PacketWrapper msg) {
        HANDLERS.get(msg.packetType).receive(MinecraftClient.getInstance(), MinecraftClient.getInstance().getNetworkHandler(), new PacketByteBuf(msg.data), null);
    }

    public interface PlayChannelHandler {
        void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    }
}
