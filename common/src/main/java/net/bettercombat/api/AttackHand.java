package net.bettercombat.api;

import net.minecraft.item.ItemStack;

public record AttackHand(
        WeaponAttributes.Attack attack,
        ComboState combo,
        boolean isOffHand,
        WeaponAttributes attributes,
        ItemStack itemStack) { }
