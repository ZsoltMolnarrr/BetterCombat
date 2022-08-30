package net.bettercombat.api;

import javax.annotation.Nullable;

public interface AttributesOwner {
    @Nullable
    WeaponAttributes getWeaponAttributes();
    void setWeaponAttributes(@Nullable WeaponAttributes weaponAttributes);
}
