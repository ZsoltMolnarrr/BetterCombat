package net.bettercombat.client.compat;

import net.bettercombat.Platform;

public class CompatibilityFlags {
    public static boolean usePehkui = false;

    public static void initialize() {
        if (Platform.isModLoaded("pehkui")) {
            usePehkui = true;
            PehkuiHelper.load();
        }
        FirstPersonAnimationCompatibility.setup();
    }
}
