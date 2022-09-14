package net.bettercombat.logic;

import net.bettercombat.api.WeaponAttributes;

import javax.annotation.Nullable;

public interface ItemStackNBTWeaponAttributes {
    boolean hasInvalidAttributes();
    void setInvalidAttributes(boolean invalid);
    @Nullable
    WeaponAttributes getWeaponAttributes();
    void setWeaponAttributes(@Nullable WeaponAttributes weaponAttributes);
}
