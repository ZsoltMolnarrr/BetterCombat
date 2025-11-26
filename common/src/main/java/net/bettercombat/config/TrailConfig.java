package net.bettercombat.config;

import net.bettercombat.api.fx.ConditionalTrailAppearance;
import net.bettercombat.api.fx.ParticlePlacement;

import java.util.LinkedHashMap;
import java.util.List;

public class TrailConfig {
    public ConditionalTrailAppearance trail_appearance = new ConditionalTrailAppearance();
    public LinkedHashMap<String, List<ParticlePlacement>> animation_based = new LinkedHashMap<>();

    public TrailConfig() { }
    public TrailConfig(ConditionalTrailAppearance trail_appearance, LinkedHashMap<String, List<ParticlePlacement>> animation_based) {
        this.trail_appearance = trail_appearance;
        this.animation_based = animation_based;
    }
}
