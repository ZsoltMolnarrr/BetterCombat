package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Platform {
    public static final boolean Fabric;
    public static final boolean Forge;

    static
    {
        Fabric = getPlatformType() == Type.FABRIC;
        Forge  = getPlatformType() == Type.FORGE;
    }

    public enum Type { FABRIC, FORGE }

    @ExpectPlatform
    protected static Type getPlatformType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modid) {
        throw new AssertionError();
    }
}
