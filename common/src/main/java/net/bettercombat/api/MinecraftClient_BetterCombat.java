package net.bettercombat.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.hit.HitResult.Type.ENTITY;

/**
 * Extension for `MinecraftClient`.
 * Example usage:
 * ((MinecraftClient_BetterCombat)MinecraftClient.getInstance()).getComboCount();
 */
public interface MinecraftClient_BetterCombat {
    int getComboCount();

    default boolean hasTargetsInReach() {
        return false; // Testing
    }

    @Nullable
    default Entity getCursorTarget() {
        // Testing
        //var client = (MinecraftClient)this;
        //if (client.crosshairTarget != null && client.crosshairTarget.getType() == ENTITY) {
        //  return ((EntityHitResult)client.crosshairTarget).getEntity();
        //}
        return null;
    }

    default int getUpswingTicks() {
        return 0; // Testing
    }

    default float getSwingProgress() {
        return 0F; // Testing
    }

    default boolean isWeaponSwingInProgress() {
        return false;  // Testing
    }

    default void cancelUpswing() {
        // Testing
    }
}
