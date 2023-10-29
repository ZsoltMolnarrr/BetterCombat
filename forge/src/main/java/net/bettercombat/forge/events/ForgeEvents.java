package net.bettercombat.forge.events;


import net.bettercombat.BetterCombat;
import net.bettercombat.mixin.PlayerEntityAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterCombat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if (!event.getEntity().getWorld().isClient())
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

    @SubscribeEvent
    public static void onHandSwap(LivingSwapItemsEvent.Hands event){
        if (event.getEntity() instanceof PlayerEntity player) {
            var offHandStack = ((PlayerEntityAccessor)player).getInventory().offHand.get(0);
            event.setItemSwappedToOffHand(player.getMainHandStack());
            event.setItemSwappedToMainHand(offHandStack);
        }
    }
}
