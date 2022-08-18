package net.bettercombat.helper.events.forge;


import net.bettercombat.BetterCombat;
import net.bettercombat.helper.events.HudRenderCallback;
import net.bettercombat.helper.events.ItemTooltipCallback;
import net.minecraft.client.item.TooltipContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterCombat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event){
        ItemTooltipCallback.onTooltip.forEach((action) -> action.accept(event.getItemStack(), event.getFlags(), event.getToolTip()));
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event){
        HudRenderCallback.onRenderHud.forEach((action) -> action.accept(event.getPoseStack(), event.getPartialTick()));
    }
}
