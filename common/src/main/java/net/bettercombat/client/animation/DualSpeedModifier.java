package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;

public class DualSpeedModifier extends AbstractModifier {
    public float speed = 1;

    public float change = 1;
    public float speed2 = 1;

    private float delta = 0;

    private float shiftedDelta = 0;

    private float elapsed = 0;

    public DualSpeedModifier(float speed) {
        if (!Float.isFinite(speed)) throw new IllegalArgumentException("Speed must be a finite number");
        this.speed = speed;
    }

    public void set(float speed, float change, float speed2) {
        this.speed = speed;
        this.change = change;
        this.speed2 = speed2;
        this.elapsed = 0;
    }

    @Override
    public void tick() {
        float delta = 1f - this.delta;
        this.delta = 0;
        step(delta);
        this.elapsed += 1;
    }

    private float accurateElapsed(float delta) {
        return elapsed + delta;
    }

    @Override
    public void setupAnim(float tickDelta) {
        float delta = tickDelta - this.delta; //this should stay positive
        this.delta = tickDelta;
        if (accurateElapsed(tickDelta) >= change) {
            System.out.println("Switching gear at: " + accurateElapsed(tickDelta) + ", speed: " + speed + ", speed2: " + speed2);
            speed = speed2;
        }
        step(delta);
    }

    protected void step(float delta) {
        delta *= speed;
        delta += shiftedDelta;
        System.out.println("step: " + delta);
        while (delta > 1) {
            delta -= 1;
            super.tick();
        }
        super.setupAnim(delta);
        this.shiftedDelta = delta;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        return super.get3DTransform(modelName, type, shiftedDelta, value0);
    }
}
