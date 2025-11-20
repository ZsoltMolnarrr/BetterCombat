package net.bettercombat.config;

import net.bettercombat.api.fx.ParticleSettings;
import net.bettercombat.api.fx.TrailAppearance;

import java.util.LinkedHashMap;
import java.util.List;

public class TrailConfig {
    public TrailAppearance default_appearance = new TrailAppearance();
    public LinkedHashMap<String, List<ParticleSettings>> animation_based = new LinkedHashMap<>();

    public TrailConfig() { }
    public TrailConfig(TrailAppearance default_appearance, LinkedHashMap<String, List<ParticleSettings>> animation_based) {
        this.default_appearance = default_appearance;
        this.animation_based = animation_based;
    }
}
