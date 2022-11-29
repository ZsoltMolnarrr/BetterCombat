package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;

public class DualSpeedModifier extends SpeedModifier {
    public float transition = 1;
    public float speed2 = 1;
    private float elapsed = 0;
    private boolean switched = false;

    public void set(float speed, float transition, float speed2) {
        this.speed = speed;
        this.transition = transition;
        this.speed2 = speed2;
        this.elapsed = 0;
        this.switched = false;
    }

    private float elapsed(float delta) {
        return elapsed + delta;
    }

    @Override
    public void tick() {
        super.tick();
        this.elapsed += 1;
    }

    @Override
    public void setupAnim(float tickDelta) {
        if (!switched && elapsed(tickDelta) >= transition) {
            speed = speed2;
            switched = true;
        }
        super.setupAnim(tickDelta);
    }
}