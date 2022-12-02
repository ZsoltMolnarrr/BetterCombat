package net.bettercombat.api;

import net.bettercombat.BetterCombat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public record AttackHand(
        WeaponAttributes.Attack attack,
        ComboState combo,
        boolean isOffHand,
        WeaponAttributes attributes,
        ItemStack itemStack) {
    public double upswingRate() {
        return MathHelper.clamp(attack.upswing(), 0, 1) * BetterCombat.config.getUpswingMultiplier();
    }
}
