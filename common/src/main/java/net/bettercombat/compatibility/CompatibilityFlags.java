package net.bettercombat.compatibility;

import net.bettercombat.Platform;
import net.bettercombat.client.compat.PehkuiHelper;

public class CompatibilityFlags {
    public static boolean usePehkui = false;

    public static void initialize() {
        if (Platform.isModLoaded("pehkui")) {
            usePehkui = true;
            PehkuiHelper.load();
        }
    }
}
