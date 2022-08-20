package net.fabricmc.fabric.api.client.item.v1;

import net.bettercombat.forge.events.ClientHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public interface ItemTooltipCallback {
    Event<ItemTooltipCallback> EVENT = new ClientHelper.TooltipEvent();

    void getTooltip(ItemStack stack, TooltipContext context, List<Text> lines);

}
