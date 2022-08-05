package net.bettercombat.client;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.Config;
import net.bettercombat.BetterCombat;
import net.minecraft.text.TextColor;


public class ClientConfig extends Config {
    ClientConfig() {
        super(BetterCombat.MODID, new String[]{"client"});
    }

    @ConfigEntry
    public boolean isHoldToAttackEnabled = true;
    @ConfigEntry
    public boolean isMiningWithWeaponsEnabled = true;
    @ConfigEntry
    public boolean isSwingThruGrassEnabled = true;
    @ConfigEntry
    public boolean isHighlightCrosshairEnabled = false;
    @ConfigEntry
    public TextColor hudHighlightColor = TextColor.fromRgb(0xFF0000);
    @ConfigEntry
    public boolean isShowingArmsInFirstPerson = false;
    @ConfigEntry
    public boolean isSmoothAnimationTransitionEnabled = true;
    @ConfigEntry
    public boolean isTooltipAttackRangeEnabled = true;
}
