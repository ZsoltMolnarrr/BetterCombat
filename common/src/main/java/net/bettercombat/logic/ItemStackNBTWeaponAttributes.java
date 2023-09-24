package net.bettercombat.logic;

import net.bettercombat.api.WeaponAttributes;
import org.jetbrains.annotations.Nullable;

public interface ItemStackNBTWeaponAttributes {
    boolean hasInvalidAttributes();
    void setInvalidAttributes(boolean invalid);
    @Nullable
    WeaponAttributes getWeaponAttributes();
    void setWeaponAttributes(@Nullable WeaponAttributes weaponAttributes);
}
