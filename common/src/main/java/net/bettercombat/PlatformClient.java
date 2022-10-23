package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.List;

public class PlatformClient {
    @ExpectPlatform
    public static void registerKeyBindings(List<KeyBinding> keyBindings) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static void onEmptyLeftClick(PlayerEntity player) {
        throw new AssertionError();
    }
}
