package net.bettercombat.client;

public interface PlayerAnimatable {
    void playAttackAnimation(String name, boolean isOffHand);
    void stopAnimation();
}
