package net.bettercombat.client;

import net.bettercombat.network.ServerNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class ClientNetwork {
    public static void initializeHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ServerNetwork.AttackAnimation.ID, (client, handler, buf, responseSender) -> {
            var packet = ServerNetwork.AttackAnimation.read(buf);
            client.execute(() -> {
                var entity = client.world.getEntityById(packet.playerId());
                if (entity instanceof PlayerEntity) {
                    ((PlayerExtension)entity).animate(packet.animationName());
                }
            });
        });
    }
}
