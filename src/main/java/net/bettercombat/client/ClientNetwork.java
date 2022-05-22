package net.bettercombat.client;

import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class ClientNetwork {
    public static void initializeHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.ID, (client, handler, buf, responseSender) -> {
            var packet = Packets.AttackAnimation.read(buf);
            client.execute(() -> {
                var entity = client.world.getEntityById(packet.playerId());
                if (entity instanceof PlayerEntity) {
                    ((PlayerExtension)entity).animate(packet.animationName());
                }
            });
        });
    }
}
