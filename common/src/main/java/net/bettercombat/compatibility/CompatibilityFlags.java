package net.bettercombat.compatibility;

import net.bettercombat.Platform;

public class CompatibilityFlags {
    public static boolean firstPersonRender = true;
    public static boolean usePehkui = false;

    public static void initialize() {
        if (Platform.isModLoaded("firstperson")) {
            CompatibilityFlags.firstPersonRender = false;
        }
        if (Platform.isModLoaded("pehkui")) {
            usePehkui = true;
        }
    }
}
