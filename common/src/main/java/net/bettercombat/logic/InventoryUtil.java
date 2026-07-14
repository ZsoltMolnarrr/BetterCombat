package net.bettercombat.logic;

import net.bettercombat.mixin.player.PlayerInventoryAccessor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryUtil {
    public static ItemStack getOffHandSlotStack(Player player) {
        return ((PlayerInventoryAccessor) player.getInventory()).getEquipment().get(Inventory.EQUIPMENT_SLOT_MAPPING.get(Inventory.SLOT_OFFHAND));
    }

    public static void setOffHandSlotStack(Player player, ItemStack stack) {
        ((PlayerInventoryAccessor) player.getInventory()).getEquipment().set(Inventory.EQUIPMENT_SLOT_MAPPING.get(Inventory.SLOT_OFFHAND), stack);
    }
}
