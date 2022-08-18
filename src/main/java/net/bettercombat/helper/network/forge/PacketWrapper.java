package net.bettercombat.helper.network.forge;

import io.netty.buffer.ByteBuf;
import net.bettercombat.BetterCombat;
import net.bettercombat.helper.network.ClientPlayNetworking;
import net.bettercombat.helper.network.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketWrapper {
    boolean isClientBound;
    public final Identifier packetType;
    public final ByteBuf data;
    public PacketWrapper(boolean isClientBound, Identifier packetType, ByteBuf data){
        this.isClientBound = isClientBound;
        this.packetType = packetType;
        this.data = data;
    }

    public static void encode(PacketWrapper msg, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBoolean(msg.isClientBound);
        packetByteBuf.writeIdentifier(msg.packetType);
        packetByteBuf.writeBytes(msg.data);
    }

    public static PacketWrapper decode(PacketByteBuf packetByteBuf) {
        return new PacketWrapper(packetByteBuf.readBoolean(), packetByteBuf.readIdentifier(), packetByteBuf.readBytes(packetByteBuf.readableBytes()));
    }

    public static void handle(PacketWrapper msg, Supplier<NetworkEvent.Context> contextSupplier) {
        if (msg.isClientBound) ClientPlayNetworking.handle(msg);
        else ServerPlayNetworking.handle(msg, contextSupplier.get().getSender());
    }
}
