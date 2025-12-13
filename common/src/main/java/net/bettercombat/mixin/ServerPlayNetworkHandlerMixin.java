package net.bettercombat.mixin;

import net.bettercombat.logic.InventoryUtil;
import net.bettercombat.mixin.player.PlayerEntityAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Redirect(method = "onPlayerAction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"),
            require = 0) // NeoForge (handled in event listener)
    public ItemStack getStackInHand(ServerPlayerEntity instance, Hand hand) {
        var player = instance;
        ItemStack result = null;
        switch (hand) {
            case MAIN_HAND -> {
                result = ((PlayerEntityAccessor)player).getInventory().getSelectedStack();
            }
            case OFF_HAND -> {
                result = InventoryUtil.getOffHandSlotStack(player);
            }
        }
        return result;
    }
}
