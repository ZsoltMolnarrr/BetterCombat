package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;

import java.util.List;

public class TransmissionSpeedModifier extends SpeedModifier {
    private float elapsed = 0;

    public List<Gear> gears = List.of();
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
    public void tick() {
        super.tick();
        this.elapsed += 1;
    }

    @Override
    public void setupAnim(float tickDelta) {
        var time = elapsed(tickDelta);
        for (var gear: gears) {
            if (time > gear.time) {
                speed = gear.speed();
            }
        }
        super.setupAnim(tickDelta);
    }
}