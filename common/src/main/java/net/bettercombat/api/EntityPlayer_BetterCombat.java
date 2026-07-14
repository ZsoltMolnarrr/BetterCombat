package net.bettercombat.api;

import org.jetbrains.annotations.Nullable;

public interface EntityPlayer_BetterCombat {
    @Nullable
    AttackHand getCurrentAttack();

    String getMainHandIdleAnimation();
    String getOffHandIdleAnimation();

    /**
     * See {@link CombatFlags} for flag values and helpers.
     */
    byte getCombatFlags();

    /**
     * Server-side only. Clients receive the value via entity tracking.
     */
    void setCombatFlags(byte flags);
}
