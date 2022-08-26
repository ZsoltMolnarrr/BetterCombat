package net.bettercombat.compatibility;

import net.bettercombat.Platform;

import java.util.function.Supplier;

public class CompatibilityFlags {
    public static boolean firstPersonRender() {
        return firstPersonRender.get();
    }
    public static Supplier<Boolean> firstPersonRender = () -> { return true; };
    public static boolean usePehkui = false;

    public static void initialize() {
        if (doesClassExist("dev.tr7zw.firstperson.FirstPersonModelCore")) {
            firstPersonRender = FirstPersonModelHelper.isDisabled();
        }
        if (Platform.isModLoaded("pehkui")) {
            usePehkui = true;
        }
    }

    /**
     * Checks if a class exists or not
     * @param name
     * @return
     */
    protected static boolean doesClassExist(String name) {
        try {
            if(Class.forName(name) != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {}
        return false;
    }
}
