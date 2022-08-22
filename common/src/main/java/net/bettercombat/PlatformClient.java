package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

public class PlatformClient {
    @ExpectPlatform
    public static void registerKeyBindings(List<KeyBinding> keyBindings) {
        throw new AssertionError();
    }
}
