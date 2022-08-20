package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface PacketSender {
    void sendPacket(Identifier channel, PacketByteBuf buf);
}
