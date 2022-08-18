package net.bettercombat.helper.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class PacketByteBufs {
    public static PacketByteBuf create() {
        return new PacketByteBuf(Unpooled.buffer());
    }
}
