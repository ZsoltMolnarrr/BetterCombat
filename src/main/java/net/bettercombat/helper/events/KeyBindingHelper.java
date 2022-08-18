package net.bettercombat.helper.events;

import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class KeyBindingHelper {
    public static List<KeyBinding> keys = new ArrayList<>();

    public static void registerKeyBinding(KeyBinding key) {
        keys.add(key);
    }
}
