package net.bettercombat.attack;

import net.bettercombat.api.WeaponAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public record AttackHand(WeaponAttributes.Attack attack, boolean isOffHand, WeaponAttributes attributes, ItemStack itemStack) {
    public double upswingRate() {
        return MathHelper.clamp(attack.upswing(), 0, 1);
    }
}
