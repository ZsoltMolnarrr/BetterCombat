package net.bettercombat.mixin;

import net.bettercombat.api.component.BetterCombatDataComponents;
import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataComponentTypes.class)
public class DataComponentTypesMixin {
    // Static class init tail inject
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void static_init_TAIL_BetterCombat(CallbackInfo ci) {
        var asd = BetterCombatDataComponents.WEAPON_PRESET_ID;
    }
}