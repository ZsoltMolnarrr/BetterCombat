package ca.lukegrahamlandry.bettercombat.events;


import net.bettercombat.BetterCombat;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterCombat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event){
        ClientHelper.onTooltip.forEach((action) -> action.getTooltip(event.getItemStack(), event.getFlags(), event.getToolTip()));
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event){
        ClientHelper.onRenderHud.forEach((action) -> action.onHudRender(event.getPoseStack(), event.getPartialTick()));
    }
}
