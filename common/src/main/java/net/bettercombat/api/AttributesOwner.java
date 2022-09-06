package net.bettercombat.api;

import javax.annotation.Nullable;

public interface AttributesOwner {
    boolean hasInvalidAttributes();
    void setInvalidAttributes(boolean invalid);
    @Nullable
    WeaponAttributes getWeaponAttributes();
    void setWeaponAttributes(@Nullable WeaponAttributes weaponAttributes);
}
