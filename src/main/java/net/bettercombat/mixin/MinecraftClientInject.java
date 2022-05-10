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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

        MinecraftClient client = thisClient();
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);

            if (attributes != null) {
//                if (client.crosshairTarget != null) {
//                    if (client.crosshairTarget.getType() == BLOCK) {
//                        return;
//                    }
//                }

                // info.setReturnValue(false);
                info.cancel();

                // MinecraftClientAccessor clientAccessor = ((MinecraftClientAccessor) MinecraftClient.getInstance());
                if (client.player.getAttackCooldownProgress(0) < 1) {
                    System.out.println("Attack is on cooldown");
                    return;
                }

                ((PlayerExtension) client.player).animate("slash");

                client.player.resetLastAttackedTicks();
            }
        }
    }


//    @ModifyVariable(method = "handleBlockBreaking(Z)V", at = @At("HEAD"), ordinal = 0)
//    private boolean handleBlockBreaking_Pre(boolean value) {
//        if (attackCooldownBackup > 0) {
//            System.out.println("Preventing reset");
//            attackCooldownBackup = 0;
//            return true;
//        }
//        return false;
//    }

    @Inject(method = "tick",at = @At("TAIL"))
    public void tick_Tail(CallbackInfo ci) {
//        MinecraftClientAccessor clientAccessor = ((MinecraftClientAccessor) MinecraftClient.getInstance());
//        myAttackCooldown--;
        // System.out.println("Current cooldown:" + clientAccessor.getAttackCooldown());


        MinecraftClient client = thisClient();
        if (client.player != null && client.world != null) {
            System.out.println("Weapon cooldown:" + client.player.getAttackCooldownProgress(0));
        }
    }
}
