package net.bettercombat.api;

import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.phys.HitResult.Type.ENTITY;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

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
        var client = (Minecraft)this;
        if (client.hitResult != null && client.hitResult.getType() == ENTITY) {
            return ((EntityHitResult)client.hitResult).getEntity();
        }
        return null;
    }

    int getUpswingTicks();
    float getSwingProgress();
    default boolean isWeaponSwingInProgress() {
        return getSwingProgress() < 1F;
    }
    @Nullable AttackHand getCurrentAttackHand();
    default WeaponAttributes.Attack getCurrentAttack() {
        var attackHand = getCurrentAttackHand();
        if (attackHand == null) {
            return null;
        }
        return attackHand.attack();
    }

    void cancelUpswing();
}
