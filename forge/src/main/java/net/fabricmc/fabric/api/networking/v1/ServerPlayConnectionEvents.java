package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;

public class ServerPlayConnectionEvents {
    public static List<TriConsumer<ServerPlayNetworkHandler, PacketSender, MinecraftServer>> onPlayerJoined = new ArrayList<>();
    public static final PlayerJoinEvent JOIN = new PlayerJoinEvent();

    public static class PlayerJoinEvent {
        public void register(TriConsumer<ServerPlayNetworkHandler, PacketSender, MinecraftServer> listener){
            onPlayerJoined.add(listener);
        }
    }

    public interface PacketSender {
        void sendPacket(Identifier id, PacketByteBuf data);
    }

}
