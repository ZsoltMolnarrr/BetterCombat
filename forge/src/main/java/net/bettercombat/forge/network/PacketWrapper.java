package net.bettercombat.forge.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public static void handle(PacketWrapper msg, CustomPayloadEvent.Context context) {
        if (msg.isClientBound) ClientPlayNetworking.handle(msg);
        else ServerPlayNetworking.handle(msg, context.getSender());
        context.setPacketHandled(true);
    }
}
