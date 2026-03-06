package net.bettercombat.logic;

import net.bettercombat.api.AttackHand;

public record WeaponSwing(AttackHand attackHand, int startedAt, int upswingTicks, float duration) {
    public int ticksLeft(int time) {
        return startedAt + durationTicks() - time;
    }
    public int upswingTicksLeft(int time) {
        return startedAt + upswingTicks - time;
    }
    public int durationTicks() {
        return Math.round(duration);
    }
    public boolean isValid(int time) {
        return time >= startedAt && time <= startedAt + (durationTicks() + 1); // +1 for padding
    }
}
