package net.fabricmc;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemTooltipCallback {
    public static List<TriConsumer<ItemStack, TooltipContext, List<Text>>> onTooltip = new ArrayList<>();
    public static final TooltipEvent EVENT = new TooltipEvent();

    public static class TooltipEvent {
        public void register(TriConsumer<ItemStack, TooltipContext, List<Text>> listener){
            onTooltip.add(listener);
        }
    }

}
