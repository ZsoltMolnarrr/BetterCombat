package net.bettercombat.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class ClientNetwork {
    public static void handleWeaponRegistrySync(Packets.WeaponRegistrySync packet) {
        WeaponRegistry.decodeRegistry(packet);
    }

    public static void handleConfigSync(Packets.ConfigSync packet) {
        BetterCombatMod.LOGGER.info("Received config sync packet");
        BetterCombatMod.config = packet.deserialized();
        BetterCombatClientMod.ENABLED = true;
    }

    public static void handleAttackAnimation(Packets.AttackAnimation packet) {
        var client = Minecraft.getInstance();
        client.execute(() -> {
            var entity = client.level.getEntity(packet.playerId());
            if (entity instanceof Player player
                    // Avoid local playback, unless replay mod is loaded
                    && (player != client.player || Platform.isModLoaded("replaymod")) ) {
                var animatable = (PlayerAttackAnimatable) entity;
                if (packet.animationName().equals(Packets.AttackAnimation.StopSymbol)) {
                    animatable.stopAttackAnimation(packet.length());
                } else {
                    animatable.playAttackAnimation(packet.animationName(), packet.animatedHand(), packet.length(), packet.upswing());
                    animatable.playAttackParticles(
                            packet.animatedHand() == AnimatedHand.OFF_HAND,
                            packet.weaponRange(),
                            packet.upswingTicks(),
                            packet.particles().particles(),
                            packet.particles().appearance()
                    );
                }
            }
        });
    }

    public static void handleAttackSound(Packets.AttackSound packet) {
        var client = Minecraft.getInstance();
        client.execute(() -> {
            try {
                if (BetterCombatClientMod.config.weaponSwingSoundVolume == 0) {
                    return;
                }

                var soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(packet.soundId()));
                var configVolume = BetterCombatClientMod.config.weaponSwingSoundVolume;
                var volume = packet.volume() * ((float) Math.min(Math.max(configVolume, 0), 100) / 100F);
                client.level.playLocalSound(
                        packet.x(),
                        packet.y(),
                        packet.z(),
                        soundEvent,
                        SoundSource.PLAYERS,
                        volume,
                        packet.pitch(),
                        true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
