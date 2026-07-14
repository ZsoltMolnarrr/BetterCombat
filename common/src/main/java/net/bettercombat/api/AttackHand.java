package net.bettercombat.api;

import net.bettercombat.BetterCombatMod;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public record AttackHand(
        WeaponAttributes.Attack attack,
        ComboState combo,
        boolean isOffHand,
        WeaponAttributes attributes,
        ItemStack itemStack) {
    public double upswingRate() {
        return Mth.clamp(attack.upswing(), 0, 1) * BetterCombatMod.config.getUpswingMultiplier();
    }
}
