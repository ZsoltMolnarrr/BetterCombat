package net.bettercombat.client.misc;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemStackViewerPlayer {
    void betterCombat_setViewedItemStack(@Nullable ItemStack itemStack);
    ItemStack betterCombat_getViewedItemStack();
}
