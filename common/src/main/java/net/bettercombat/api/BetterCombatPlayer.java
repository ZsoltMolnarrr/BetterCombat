package net.bettercombat.api;

import org.jetbrains.annotations.Nullable;

public interface BetterCombatPlayer {
    @Nullable
    AttackHand getCurrentAttack();
}
