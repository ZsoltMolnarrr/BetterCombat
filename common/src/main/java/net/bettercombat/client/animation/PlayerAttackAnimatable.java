package net.bettercombat.client.animation;

import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.logic.AnimatedHand;

import java.util.List;

public interface PlayerAttackAnimatable {
    void updateAnimationsOnTick();
    void playAttackAnimation(String name, AnimatedHand hand, float length, float upswing);
    void playAttackParticles(boolean isOffHand, float weaponRange, int delay, List<ParticlePlacement> particles, TrailAppearance appearance);
    void stopAttackAnimation(float length);
}
