package net.bettercombat.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.List;

public class PlatformClientImpl {
    public static void registerKeyBindings(List<KeyBinding> keyBindings) {
        for(var keybinding: keyBindings) {
            KeyBindingHelper.registerKeyBinding(keybinding);
        }
    }
    public static void onEmptyLeftClick(PlayerEntity player) {
        // Do nothing, this event does not exist on Fabric
    }
}
