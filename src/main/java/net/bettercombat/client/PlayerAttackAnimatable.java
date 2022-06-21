package net.bettercombat.client;

public interface PlayerAttackAnimatable {
    void updateAnimationsOnTick();
    void playAttackAnimation(String name, boolean isOffHand);
    void stopAttackAnimation();
}
