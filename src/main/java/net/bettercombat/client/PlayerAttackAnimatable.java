package net.bettercombat.client;

public interface PlayerAttackAnimatable {
    void updateAnimationsOnTick();
    void playAttackAnimation(String name, boolean isOffHand, float length);
    void stopAttackAnimation();
}
