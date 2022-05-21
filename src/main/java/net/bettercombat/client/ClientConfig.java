package net.bettercombat.client;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.math.Color;

public class ClientConfig implements ConfigGroup {
    @ConfigEntry
    public boolean isHoldToAttackEnabled = true;
    @ConfigEntry
    public boolean isMiningWithWeaponsEnabled = true;
    @ConfigEntry
    public boolean isSwingThruGrassEnabled = true;
    @ConfigEntry
    public boolean isAlwaysShowingAttackIndicatorForWeapons = true;
    @ConfigEntry
    public boolean isHighlightCrosshairEnabled = false;
    @ConfigEntry
    public boolean isHighlightAttackIndicatorEnabled = false;
    @ConfigEntry
    public Color hudHighlightColor = Color.ofRGBA(255, 0, 0, 127);

//    public Listener listener;
//    public interface Listener {
//        void feintKeyUpdated();
//    }
//
//    public void setFeintKey(Key feintKey) {
//        this.feintKey = feintKey;
//        if (listener != null) {
//            listener.feintKeyUpdated();
//        }
//    }
}
