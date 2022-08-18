package net.fabricmc.fabric.api.client.networking.v1;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PlayerLookup {
    public static List<? extends PlayerEntity> tracking(ServerPlayerEntity player) {
        return player.world.getPlayers(); // TODO
    }

    public static List<? extends PlayerEntity> around(ServerWorld world, Vec3d origin, float distance) {
        return world.getPlayers((player) -> player.getPos().distanceTo(origin) <= distance);
    }
}
