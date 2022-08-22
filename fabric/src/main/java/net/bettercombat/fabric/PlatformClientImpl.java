package net.bettercombat.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

public class PlatformClientImpl {
    public static void registerKeyBindings(List<KeyBinding> keyBindings) {
        for(var keybinding: keyBindings) {
            KeyBindingHelper.registerKeyBinding(keybinding);
        }
    }
}
