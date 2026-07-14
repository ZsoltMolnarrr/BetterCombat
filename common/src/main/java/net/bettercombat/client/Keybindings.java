package net.bettercombat.client;

import net.bettercombat.BetterCombatMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;

public class Keybindings {
    public static KeyMapping feintKeyBinding;
    public static KeyMapping toggleMineKeyBinding;
    public static List<KeyMapping> all;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "main"));

    static {
        feintKeyBinding = new KeyMapping(
                "keybinds.bettercombat.feint",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                CATEGORY);

        toggleMineKeyBinding = new KeyMapping(
                "keybinds.bettercombat.toggle_mine_with_weapons",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                CATEGORY);

        all = List.of(feintKeyBinding, toggleMineKeyBinding);
    }
}
