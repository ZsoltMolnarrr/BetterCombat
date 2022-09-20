package net.bettercombat.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public class BetterCombatKeybindings {
    public static KeyBinding feintKeyBinding;
    public static KeyBinding toggleMineKeyBinding;
    public static List<KeyBinding> all;

    static {
        feintKeyBinding = new KeyBinding(
                "keybinds.bettercombat.feint",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                "Better Combat");

        toggleMineKeyBinding = new KeyBinding(
                "keybinds.bettercombat.toggle_mine_with_weapons",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                "Better Combat");

        all = List.of(feintKeyBinding, toggleMineKeyBinding);
    }
}
