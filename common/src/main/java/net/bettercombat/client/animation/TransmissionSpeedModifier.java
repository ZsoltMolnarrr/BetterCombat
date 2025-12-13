package net.bettercombat.client.animation;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;

import java.util.List;

public class TransmissionSpeedModifier extends SpeedModifier {
    private float elapsed = 0;

    public List<Gear> gears = List.of();

    public TransmissionSpeedModifier(float speed) {
        super(speed);
    }

    public record Gear(float time, float speed) {}

    public void set(float speed, List<Gear> gears) {
        this.speed = speed;
        this.gears = gears;
        this.elapsed = 0;
    }

    private float elapsed(float delta) {
        return elapsed + delta;
    }

    @Override
    public void tick(AnimationData state) {
        super.tick(state);
        this.elapsed += 1;
    }

    @Override
    public void setupAnim(AnimationData state) {
        float tickDelta = state.getPartialTick();
        var time = elapsed(tickDelta);
        for (var gear: gears) {
            if (time > gear.time) {
                speed = gear.speed();
            }
        }
        super.setupAnim(state);
    }
}