package net.bettercombat.fabric.network;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class FabricServerNetwork {
    public static void init() {
        // Config stage
        PayloadTypeRegistry.configurationS2C().register(Packets.ConfigSync.PACKET_ID, Packets.ConfigSync.CODEC);
        PayloadTypeRegistry.configurationS2C().register(Packets.WeaponRegistrySync.PACKET_ID, Packets.WeaponRegistrySync.CODEC);
        PayloadTypeRegistry.configurationC2S().register(Packets.Ack.PACKET_ID, Packets.Ack.CODEC);

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            // This if block is required! Otherwise the client gets stuck in connection screen
            // if the client cannot handle the packet.
            if (ServerConfigurationNetworking.canSend(handler, Packets.ConfigSync.ID)) {
                // System.out.println("Starting ConfigurationTask");
                var configJson = Packets.ConfigSync.serialize(BetterCombatMod.getConfig());
                handler.addTask(new ConfigurationTask(configJson));
            } else {
                handler.disconnect(Text.literal("Network configuration task not supported: " + ConfigurationTask.name));
            }
        });

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, Packets.WeaponRegistrySync.ID)) {
                if (WeaponRegistry.getEncodedRegistry().chunks().isEmpty()) {
                    throw new AssertionError("Weapon registry is empty!");
                }
                // System.out.println("Starting WeaponRegistrySyncTask, chunks: " + WeaponRegistry.getEncodedRegistry().chunks().size());
                handler.addTask(new WeaponRegistrySyncTask(WeaponRegistry.getEncodedRegistry()));
            } else {
                handler.disconnect(Text.literal("Network configuration task not supported: " + WeaponRegistrySyncTask.name));
            }
        });

        ServerConfigurationNetworking.registerGlobalReceiver(Packets.Ack.PACKET_ID, (packet, context) -> {
            // Warning: if you do not call completeTask, the client gets stuck!
            if (packet.code().equals(ConfigurationTask.name)) {
                context.networkHandler().completeTask(ConfigurationTask.KEY);
            }
            if (packet.code().equals(WeaponRegistrySyncTask.name)) {
                context.networkHandler().completeTask(WeaponRegistrySyncTask.KEY);
            }
        });

        // Play stage
        PayloadTypeRegistry.playS2C().register(Packets.AttackSound.PACKET_ID, Packets.AttackSound.CODEC);
        PayloadTypeRegistry.playS2C().register(Packets.AttackAnimation.PACKET_ID, Packets.AttackAnimation.CODEC);
        PayloadTypeRegistry.playC2S().register(Packets.AttackAnimation.PACKET_ID, Packets.AttackAnimation.CODEC);
        PayloadTypeRegistry.playC2S().register(Packets.C2S_AttackRequest.PACKET_ID, Packets.C2S_AttackRequest.CODEC);
        PayloadTypeRegistry.playC2S().register(Packets.C2S_BlockHit.PACKET_ID, Packets.C2S_BlockHit.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.PACKET_ID, (packet, context) -> {
            ServerNetwork.handleAttackAnimation(packet, context.server(), context.player());
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_AttackRequest.PACKET_ID, (packet, context) -> {
            ServerNetwork.handleAttackRequest(packet, context.server(), context.player(), context.player().networkHandler);
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_BlockHit.PACKET_ID, (packet, context) -> {
            ServerNetwork.handleBlockHit(packet, context.server(), context.player());
        });
    }

    public record ConfigurationTask(String configString) implements ServerPlayerConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "config";
        public static final Key KEY = new Key(name);

        @Override
        public Key getKey() {
            return KEY;
        }

        @Override
        public void sendPacket(Consumer<Packet<?>> sender) {
            var packet = new Packets.ConfigSync(this.configString);
            sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
        }
    }

    public record WeaponRegistrySyncTask(WeaponRegistry.Encoded encodedRegistry) implements ServerPlayerConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "weapon_registry";
        public static final Key KEY = new Key(name);

        @Override
        public Key getKey() {
            return KEY;
        }

        @Override
        public void sendPacket(Consumer<Packet<?>> sender) {
            var packet = new Packets.WeaponRegistrySync(encodedRegistry.compressed(), encodedRegistry.chunks());
            sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
        }
    }
}
