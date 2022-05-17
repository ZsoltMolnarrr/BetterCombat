package net.bettercombat.network;

import com.google.common.collect.Iterables;
import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public class WeaponSwingPacket {

    public record C2S_AttackRequest(int comboCount, int stack, int[] entityIds) {
        public static Identifier ID = new Identifier("C2S_REQUEST_ATTACK");
        public static void write(PacketByteBuf buffer, int comboCount, boolean useMainHand, List<Entity> entities) {
            int[] ids = new int[entities.size()];
            for(int i = 0; i < entities.size(); i++) {
                ids[i] = entities.get(i).getId();
            }
            buffer.writeInt(comboCount);
            buffer.writeInt(useMainHand ? 0 : 1);
            buffer.writeIntArray(ids);
        }

        public static C2S_AttackRequest read(PacketByteBuf buffer) {
            int comboCount = buffer.readInt();
            int stack = buffer.readInt();
            int[] ids = buffer.readIntArray();
            return new C2S_AttackRequest(comboCount, stack, ids);
        }
    }

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

        ServerPlayNetworking.registerGlobalReceiver(C2S_AttackRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null) {
                return;
            }

            C2S_AttackRequest request = C2S_AttackRequest.read(buf);
            WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
            if (attributes != null) {
                // player.getAttributes().addTemporaryModifiers();
            }

            // handler.onPlayerInteractEntity();

            // Set damage multiplier
            // Save lastAttacked
            // Iterate targets
            //   Restore lastAttacked
            //   map to interaction packet
            //   attack using packet
            // Clear damage multiplier

            // world.isPlayerInRange()
            // ServerPlayNetworking.send((ServerPlayerEntity) user, TutorialNetworkingConstants.HIGHLIGHT_PACKET_ID, PacketByteBufs.empty());
            // world.getChunkManager().sendToOtherNearbyPlayers(player, asd);
            // ServerChunkManager serverChunkManager = server.chunk
        });
    }
}
