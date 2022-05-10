package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.MeleeWeaponAttributes;
import net.bettercombat.client.PlayerExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientInject {

    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }

//    private int myAttackCooldown = 0;

    @Inject(method = "doAttack",at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        if (performAttack()) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(method = "handleBlockBreaking",at = @At("HEAD"), cancellable = true)
    private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
        MinecraftClient client = thisClient();
        if (client.options.attackKey.isPressed() && performAttack()) {
            ci.cancel();
        }
    }

    private boolean performAttack() {
        MinecraftClient client = thisClient();
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);

            if (attributes != null) {
                if (client.player.getAttackCooldownProgress(0) < 1) {
                    return true;
                }
                ((PlayerExtension) client.player).animate("slash");
                client.player.resetLastAttackedTicks();
                ((MinecraftClientAccessor) client).setAttackCooldown(10);
                return true;
            }
        }
        return false;
    }
}
