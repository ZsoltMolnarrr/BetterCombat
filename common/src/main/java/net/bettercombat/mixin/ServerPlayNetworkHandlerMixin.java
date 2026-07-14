package net.bettercombat.mixin;

import net.bettercombat.logic.InventoryUtil;
import net.bettercombat.mixin.player.PlayerEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Redirect(method = "handlePlayerAction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"),
            require = 0) // NeoForge (handled in event listener)
    public ItemStack getStackInHand(ServerPlayer instance, InteractionHand hand) {
        var player = instance;
        ItemStack result = null;
        switch (hand) {
            case MAIN_HAND -> {
                result = ((PlayerEntityAccessor)player).getInventory().getSelectedItem();
            }
            case OFF_HAND -> {
                result = InventoryUtil.getOffHandSlotStack(player);
            }
        }
        return result;
    }
}
