package net.bettercombat.forge.network;

import net.bettercombat.BetterCombat;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;

    public static void registerMessages(){
        INSTANCE = ChannelBuilder.named(new Identifier(BetterCombat.MODID, "network"))
                .networkProtocolVersion(1)
                .acceptedVersions((s, v) -> true)
                .simpleChannel();
        INSTANCE.messageBuilder(PacketWrapper.class)
                .encoder(PacketWrapper::encode)
                .decoder(PacketWrapper::decode)
                .consumerNetworkThread(PacketWrapper::handle)
                .add();
    }
}
