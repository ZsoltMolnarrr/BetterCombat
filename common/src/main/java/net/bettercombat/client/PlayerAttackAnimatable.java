package net.bettercombat.client;

import dev.kosmx.playerAnim.api.layered.IAnimation;

import java.util.Optional;

public interface PlayerAttackAnimatable {
    void updateAnimationsOnTick();
    void playAttackAnimation(String name, boolean isOffHand, float length, float upswing);
    void stopAttackAnimation();
    Optional<IAnimation> getCurrentAnimation();
}
