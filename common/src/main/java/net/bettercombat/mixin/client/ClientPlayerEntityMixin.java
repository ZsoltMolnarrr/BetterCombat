package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.misc.ItemStackViewerPlayer;
import net.bettercombat.utils.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements ItemStackViewerPlayer {
    @Shadow protected abstract boolean isCamera();

    @Inject(method = "tickMovementInput", at = @At(value = "TAIL"))
    private void tickMovement_ModifyInput(CallbackInfo ci) {
        var clientPlayer = (ClientPlayerEntity)((Object)this);
        if (!isCamera()) {
            return;
        }
        var config = BetterCombatMod.config;
        var client = (MinecraftClient_BetterCombat) MinecraftClient.getInstance();
        float multiplier = (float) Math.min(Math.max(config.movement_speed_while_attacking, 0.0), 1.0);
        var attack = client.getCurrentAttack();
        if (attack != null) {
            multiplier *= attack.movementSpeedMultiplier();
        }
//        System.out.println("Multiplier " + multiplier);
        if (multiplier == 1) {
            return;
        }
        if (clientPlayer.hasVehicle() && !config.movement_speed_effected_while_mounting) {
            return;
        }

        var swingProgress = client.getSwingProgress();
        if (swingProgress < 0.98) {
            if (config.movement_speed_applied_smoothly) {
                double p2 = 0;
                if (swingProgress <= 0.5) {
                    p2 = MathHelper.easeOutCubic(swingProgress * 2);
                } else {
                    p2 = MathHelper.easeOutCubic(1 - ((swingProgress - 0.5) * 2));
                }
                multiplier = (float) ( 1.0 - (1.0 - multiplier) * p2 );
//                var chart = "-".repeat((int)(100.0 * multiplier)) + "x";
//                System.out.println("Movement speed multiplier: " + String.format("%.4f", multiplier) + ">" + chart);
            }
            clientPlayer.forwardSpeed *= multiplier;
            clientPlayer.sidewaysSpeed *= multiplier;
        }
    }

    // MARK: ItemStackViewerPlayer
    @Unique private ItemStack bettercombat$viewedItemStack = null;
    public void betterCombat_setViewedItemStack(@Nullable ItemStack itemStack) {
        bettercombat$viewedItemStack = itemStack;
    }
    public ItemStack betterCombat_getViewedItemStack() {
        return bettercombat$viewedItemStack;
    }
}
