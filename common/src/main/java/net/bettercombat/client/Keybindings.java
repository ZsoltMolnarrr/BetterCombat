package net.bettercombat.client;

import net.bettercombat.BetterCombatMod;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.List;

public class Keybindings {
    public static KeyBinding feintKeyBinding;
    public static KeyBinding toggleMineKeyBinding;
    public static List<KeyBinding> all;

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(BetterCombatMod.ID, "main"));

    static {
        feintKeyBinding = new KeyBinding(
                "keybinds.bettercombat.feint",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                CATEGORY);

        toggleMineKeyBinding = new KeyBinding(
                "keybinds.bettercombat.toggle_mine_with_weapons",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                CATEGORY);

        all = List.of(feintKeyBinding, toggleMineKeyBinding);
    }
}
