package net.fabricmc.fabric.api.client.keybinding.v1;

import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class KeyBindingHelper {
    public static List<KeyBinding> keys = new ArrayList<>();

    public static KeyBinding registerKeyBinding(KeyBinding key) {
        keys.add(key);
        return key;
    }
}
