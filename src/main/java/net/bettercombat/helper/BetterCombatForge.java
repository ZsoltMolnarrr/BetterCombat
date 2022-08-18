package net.bettercombat.helper;

import net.bettercombat.BetterCombat;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterCombat.MODID)
public class BetterCombatForge {
    public BetterCombatForge(){
        new BetterCombat().onInitialize();
    }
}
