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
    boolean hasTargetsInReach();
    @Nullable
    default Entity getCursorTarget() {
        var client = (MinecraftClient)this;
        if (client.crosshairTarget != null && client.crosshairTarget.getType() == ENTITY) {
            return ((EntityHitResult)client.crosshairTarget).getEntity();
        }
        return null;
    }

    int getUpswingTicks();
    float getSwingProgress();
    default boolean isWeaponSwingInProgress() {
        return getSwingProgress() < 1F;
    }
    void cancelUpswing();
}
