package net.bettercombat.mixin;

import net.bettercombat.api.AttributesOwner;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AttributesOwner {
    private WeaponAttributes weaponAttributes;
    @Override
    public WeaponAttributes getWeaponAttributes() {
        return weaponAttributes;
    }

    @Override
    public void setWeaponAttributes(WeaponAttributes weaponAttributes) {
        this.weaponAttributes = weaponAttributes;
    }
}
