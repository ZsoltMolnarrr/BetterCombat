package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.bettercombat.logic.AnimatedHand;

import java.util.Optional;

public interface PlayerAttackAnimatable {
    void updateAnimationsOnTick();
    void playAttackAnimation(String name, AnimatedHand hand, float length, float upswing);
    void stopAttackAnimation();
}
