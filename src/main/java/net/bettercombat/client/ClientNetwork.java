package net.bettercombat.client;

import net.bettercombat.BetterCombat;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class ClientNetwork {
    public static void initializeHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.ID, (client, handler, buf, responseSender) -> {
            final var packet = Packets.AttackAnimation.read(buf);
            client.execute(() -> {
                var entity = client.world.getEntityById(packet.playerId());
                if (entity instanceof PlayerEntity) {
                    if (packet.animationName().equals(Packets.AttackAnimation.StopSymbol)) {
                        ((PlayerAttackAnimatable)entity).stopAttackAnimation();
                    } else {
                        ((PlayerAttackAnimatable)entity).playAttackAnimation(packet.animationName(), packet.isOffHand(), packet.length());
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.WeaponRegistrySync.ID, (client, handler, buf, responseSender) -> {
            WeaponRegistry.decodeRegistry(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.ConfigSync.ID, (client, handler, buf, responseSender) -> {
            Packets.ConfigSync.readInPlace(buf, BetterCombat.config);
        });
    }
}
