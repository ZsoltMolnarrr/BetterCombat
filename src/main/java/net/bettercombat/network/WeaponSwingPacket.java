package net.bettercombat.network;

import com.google.common.collect.Iterables;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class WeaponSwingPacket {
    public static Identifier C2S_REQUEST_SWING = new Identifier("C2S_REQUEST_SWING");
    public static Identifier S2C_PERFORM_SWING = new Identifier("C2S_REQUEST_SWING");

    public static void initializeHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(WeaponSwingPacket.C2S_REQUEST_SWING, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null) {
                return;
            }

            // world.isPlayerInRange()
            // ServerPlayNetworking.send((ServerPlayerEntity) user, TutorialNetworkingConstants.HIGHLIGHT_PACKET_ID, PacketByteBufs.empty());
            // world.getChunkManager().sendToOtherNearbyPlayers(player, asd);
            // ServerChunkManager serverChunkManager = server.chunk
        });
    }
}
