package net.bettercombat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "client")
public class ClientConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean isHoldToAttackEnabled = true;
    @ConfigEntry.Gui.Tooltip
    public boolean isMiningWithWeaponsEnabled = true;
    @ConfigEntry.Gui.Tooltip
    public boolean isSwingThruGrassEnabled = true;
    @ConfigEntry.Gui.Tooltip
    public boolean isHighlightCrosshairEnabled = false;
    @ConfigEntry.ColorPicker
    @ConfigEntry.Gui.Tooltip
    public int hudHighlightColor = 0xFF0000;
    @ConfigEntry.Gui.Tooltip
    public boolean isShowingArmsInFirstPerson = false;
    @ConfigEntry.Gui.Tooltip
    public boolean isSmoothAnimationTransitionEnabled = true;
    @ConfigEntry.Gui.Tooltip
    public boolean isTooltipAttackRangeEnabled = true;
    @ConfigEntry.Gui.Tooltip
    public boolean isSweepingParticleEnabled = true;
    @ConfigEntry.Gui.Tooltip
    public boolean isWeaponSwingSoundEnabled = true;
}
