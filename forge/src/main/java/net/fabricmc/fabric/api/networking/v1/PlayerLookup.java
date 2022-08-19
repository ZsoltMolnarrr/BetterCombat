package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

public class PlayerLookup {
    public static Collection<ServerPlayerEntity> tracking(Entity player) {
        return (Collection<ServerPlayerEntity>) player.world.getPlayers(); // TODO
    }

    public static Collection<ServerPlayerEntity> around(ServerWorld world, Vec3d origin, double distance) {
        return world.getPlayers((player) -> player.getPos().distanceTo(origin) <= distance);
    }
}
