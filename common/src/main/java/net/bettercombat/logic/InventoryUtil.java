package net.bettercombat.logic;

import net.bettercombat.mixin.player.PlayerInventoryAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtil {
    public static ItemStack getOffHandSlotStack(PlayerEntity player) {
        return ((PlayerInventoryAccessor) player.getInventory()).getEquipment().get(PlayerInventory.EQUIPMENT_SLOTS.get(PlayerInventory.OFF_HAND_SLOT));
    }

    public static void setOffHandSlotStack(PlayerEntity player, ItemStack stack) {
        ((PlayerInventoryAccessor) player.getInventory()).getEquipment().put(PlayerInventory.EQUIPMENT_SLOTS.get(PlayerInventory.OFF_HAND_SLOT), stack);
    }
}
