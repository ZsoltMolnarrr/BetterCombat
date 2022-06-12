package net.bettercombat.client;

public interface PlayerAttackAnimatable {
    void updatePose();
    void playAttackAnimation(String name, boolean isOffHand);
    void stopAttackAnimation();
}
