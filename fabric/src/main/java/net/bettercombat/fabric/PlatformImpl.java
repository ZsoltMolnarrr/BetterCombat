package net.bettercombat.fabric;

import net.bettercombat.Platform;
import net.fabricmc.loader.api.FabricLoader;

import static net.bettercombat.Platform.Type.FABRIC;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return FABRIC;
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
