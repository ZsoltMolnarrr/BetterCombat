package net.bettercombat.mixin;

import net.bettercombat.api.MeleeWeapon;
import net.bettercombat.client.PlayerExtension;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "doAttack",at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        System.out.println("This line is printed by mixin!");
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player.getMainHandStack() != null
                && client.player.getMainHandStack().getItem() instanceof MeleeWeapon) {
            // MeleeWeapon weapon = (MeleeWeapon) client.player.getMainHandStack().getItem();
            // client.getNetworkHandler().sendPacket();

            ((PlayerExtension) client.player).animatePlayer("slash");

            info.cancel();
        }
    }
}
