package net.bettercombat.client;

import net.bettercombat.BetterCombat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackSound.ID, (client, handler, buf, responseSender) -> {
            final var packet = Packets.AttackSound.read(buf);
            client.execute(() -> {
                try {
                    if (!BetterCombatClient.config.isWeaponSwingSoundEnabled) {
                        return;
                    }

                    var soundEvent = Registry.SOUND_EVENT.get(new Identifier(packet.soundId()));
                    client.world.playSound(
                            client.player,
                            packet.x(),
                            packet.y(),
                            packet.z(),
                            soundEvent,
                            SoundCategory.PLAYERS,
                            packet.volume(),
                            packet.pitch(),
                            packet.seed());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.WeaponRegistrySync.ID, (client, handler, buf, responseSender) -> {
            WeaponRegistry.decodeRegistry(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.ConfigSync.ID, (client, handler, buf, responseSender) -> {
            var config = Packets.ConfigSync.read(buf);
            // var gson = new Gson();
            // System.out.println("Received server config: " + gson.toJson(config));
            BetterCombat.config = config;
        });
    }
}
