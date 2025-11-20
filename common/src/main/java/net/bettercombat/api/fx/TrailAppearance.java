package net.bettercombat.api.fx;

import org.jetbrains.annotations.Nullable;

public class TrailAppearance {
    public String primary_color_rgba = "FFFFFF";
    public String secondary_color_rgba;
    public boolean glows = false;

    public TrailAppearance() { }
    public TrailAppearance(String primary_color_rgba, @Nullable String secondary_color_rgba, boolean glows) {
        this.primary_color_rgba = primary_color_rgba;
        this.secondary_color_rgba = secondary_color_rgba;
        this.glows = glows;
    }
}
