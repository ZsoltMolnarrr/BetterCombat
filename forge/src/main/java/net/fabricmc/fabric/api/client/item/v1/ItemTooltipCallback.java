package net.fabricmc.fabric.api.client.item.v1;

import ca.lukegrahamlandry.bettercombat.events.ClientHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ItemTooltipCallback {
    Event<ItemTooltipCallback> EVENT = new ClientHelper.TooltipEvent();

    void getTooltip(ItemStack stack, TooltipContext context, List<Text> lines);

}
