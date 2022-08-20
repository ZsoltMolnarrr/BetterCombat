package net.bettercombat.forge.events;


import net.bettercombat.BetterCombat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterCombat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if (!event.getEntity().world.isClient())
            ServerPlayConnectionEvents.onPlayerJoined.forEach((action) -> action.onPlayReady(
                ((ServerPlayerEntity)event.getEntity()).networkHandler,
                (id, data) -> ServerPlayNetworking.send((ServerPlayerEntity) event.getEntity(), id, data),
                event.getEntity().getServer()
        ));
    }

    @SubscribeEvent
    public static void onSererStart(ServerStartedEvent event){
        ServerLifecycleEvents.onServerStarted.forEach((action) -> action.onServerStarted(event.getServer()));
    }
}
