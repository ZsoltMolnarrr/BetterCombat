package net.bettercombat.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.hit.HitResult.Type.ENTITY;

public class MinecraftClientHelper {
    @Nullable
    public static Entity getCursorTarget(MinecraftClient client) {
        if (client.crosshairTarget.getType() == ENTITY) {
            return ((EntityHitResult)client.crosshairTarget).getEntity();
        }
        return null;
    }
}
