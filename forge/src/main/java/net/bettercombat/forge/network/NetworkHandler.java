package net.bettercombat.forge.network;

import net.bettercombat.BetterCombat;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.nio.channels.Channel;

public class NetworkHandler {
    public static Channel INSTANCE;
    private static int ID = 0;

    public static void registerMessages(){
//        INSTANCE = NetworkRegistry.newSimpleChannel(new Identifier(BetterCombat.MODID, "network"), () -> "1.0", s -> true, s -> true);
//        INSTANCE.registerMessage(ID++, PacketWrapper.class, PacketWrapper::encode, PacketWrapper::decode, PacketWrapper::handle);
    }
}
