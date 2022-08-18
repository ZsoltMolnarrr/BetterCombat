package net.bettercombat.helper.events.forge;


import net.bettercombat.BetterCombat;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.helper.events.ClientLifecycleEvents;
import net.bettercombat.helper.events.HudRenderCallback;
import net.bettercombat.helper.events.ItemTooltipCallback;
import net.bettercombat.helper.events.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BetterCombat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientModEvents {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        KeyBindingHelper.keys.forEach(event::register);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        new BetterCombatClient().onInitializeClient();
        ClientLifecycleEvents.onClientStarted.forEach((action) -> action.accept(MinecraftClient.getInstance()));
    }
}
