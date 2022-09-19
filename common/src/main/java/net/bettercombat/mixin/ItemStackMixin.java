package net.bettercombat.mixin;

import net.bettercombat.logic.ItemStackNBTWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackNBTWeaponAttributes {
    private boolean hasInvalidAttributes = false;
    public boolean hasInvalidAttributes() {
        return hasInvalidAttributes;
    }
    public void setInvalidAttributes(boolean invalid) {
        this.hasInvalidAttributes = invalid;
    }

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
