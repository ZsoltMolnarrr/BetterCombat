package net.bettercombat.neoforge;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.mixin.player.PlayerEntityAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingSwapItemsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = BetterCombatMod.ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeEvents {
    @SubscribeEvent
    public static void register(final ServerAboutToStartEvent event) {
        BetterCombatMod.loadWeaponAttributes(event.getServer());
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
