package net.bettercombat.neoforge.network;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.ClientNetwork;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Consumer;

@EventBusSubscriber(modid = BetterCombatMod.ID)
public class NetworkEvents {
    @SubscribeEvent
    public static void register(final RegisterConfigurationTasksEvent event) {
        event.register(new ConfigurationTask());
        event.register(new WeaponRegistrySyncTask());
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // Server config stage

        registrar.configurationToServer(Packets.Ack.PACKET_ID, Packets.Ack.CODEC, (payload, context) -> {
            if (payload.code().equals(ConfigurationTask.name)) {
                context.finishCurrentTask(ConfigurationTask.KEY);
            }
            if (payload.code().equals(WeaponRegistrySyncTask.name)) {
                context.finishCurrentTask(WeaponRegistrySyncTask.KEY);
            }
        });

        // Server play stage

        registrar.playToServer(Packets.C2S_AttackRequest.PACKET_ID, Packets.C2S_AttackRequest.CODEC, (packet, context) -> {
            var player = (ServerPlayer)context.player();
            var server = player.level().getServer();
            var vanillaHandler = player.connection;
            ServerNetwork.handleAttackRequest(packet, server, player, vanillaHandler);
        });

        registrar.playToServer(Packets.C2S_BlockHit.PACKET_ID, Packets.C2S_BlockHit.CODEC, (packet, context) -> {
            var player = (ServerPlayer)context.player();
            var server = player.level().getServer();
            ServerNetwork.handleBlockHit(packet, server, player);
        });

        // Shared play stage

        registrar.playBidirectional(Packets.AttackAnimation.PACKET_ID, Packets.AttackAnimation.CODEC, (packet, context) -> {
            var player = (ServerPlayer) context.player();
            var server = player.level().getServer();
            ServerNetwork.handleAttackAnimation(packet, server, player);
        }, (packet, context) -> {
            ClientNetwork.handleAttackAnimation(packet);
        });

        // Client config stage

        registrar.configurationToClient(Packets.ConfigSync.PACKET_ID, Packets.ConfigSync.CODEC, (packet, context) -> {
            ClientNetwork.handleConfigSync(packet);
            context.reply(new Packets.Ack(ConfigurationTask.name));
        });

        registrar.configurationToClient(Packets.WeaponRegistrySync.PACKET_ID, Packets.WeaponRegistrySync.CODEC, (packet, context) -> {
            ClientNetwork.handleWeaponRegistrySync(packet);
            context.reply(new Packets.Ack(WeaponRegistrySyncTask.name));
        });

        // Client play stage

        registrar.playToClient(Packets.AttackSound.PACKET_ID, Packets.AttackSound.CODEC, (packet, context) -> {
            ClientNetwork.handleAttackSound(packet);
        });
    }

    public record ConfigurationTask() implements ICustomConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "config";
        public static final Type KEY = new Type(name);

        @Override
        public Type type() {
            return KEY;
        }

        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            var configString = Packets.ConfigSync.serialize(BetterCombatMod.config);
            var packet = new Packets.ConfigSync(configString);
            sender.accept(packet);
        }
    }

    public record WeaponRegistrySyncTask() implements ICustomConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "weapon_registry";
        public static final Type KEY = new Type(name);

        @Override
        public Type type() {
            return KEY;
        }

        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            var encodded = WeaponRegistry.getEncodedRegistry();
            var packet = new Packets.WeaponRegistrySync(encodded.compressed(), encodded.chunks());
            sender.accept(packet);
        }
    }
}