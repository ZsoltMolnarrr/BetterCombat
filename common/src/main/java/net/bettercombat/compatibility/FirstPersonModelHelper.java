package net.bettercombat.compatibility;

import dev.tr7zw.firstperson.api.FirstPersonAPI;

import java.util.function.Supplier;

public class FirstPersonModelHelper {
    public static Supplier<Boolean> isDisabled() {
        return (() -> {
            return !FirstPersonAPI.isEnabled();
        });
    }
}
