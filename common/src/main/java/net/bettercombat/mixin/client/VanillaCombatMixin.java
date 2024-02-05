package net.bettercombat.mixin.client;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.*;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;

@Mixin(MinecraftClient.class)
public abstract class VanillaCombatMixin implements MinecraftClient_BetterCombat {
    @Shadow public ClientWorld world;
    @Shadow @Nullable public ClientPlayerEntity player;

    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null && attributes.attacks() != null) {
            startUpswing(attributes);
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"))
    private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null && attributes.attacks() != null) {
            boolean isPressed = client.options.attackKey.isPressed();
            if(isPressed && isTargetingMineableBlock()) {
                cancelWeaponSwing();
            }
        }
    }

    private void resetComboIfNeeded() {
        // Combo timeout
        if(lastAttacked > comboReset && getComboCount() > 0) {
            setComboCount(0);
        }
    }

    private boolean isTargetingMineableBlock() {
        MinecraftClient client = thisClient();
        HitResult crosshairTarget = client.crosshairTarget;
        return crosshairTarget != null && crosshairTarget.getType() == BLOCK;
    }

    private ItemStack upswingStack;
    private int lastAttacked = 1000;
    private int comboReset = 0;

    private void startUpswing(WeaponAttributes attributes) {
        // Guard conditions

        if (player.isRiding()) {
            // isRiding is `isHandsBusy()` according to official mappings
            // Support for revival mod
            return;
        }

        var hand = getCurrentHand();
        if (hand == null) { return; }
        float upswingRate = (float) hand.upswingRate();

        lastAttacked = 0;
        upswingStack = player.getMainHandStack();
        float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(player); // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownLengthTicks`
        this.comboReset = Math.round(attackCooldownTicksFloat * BetterCombat.config.combo_reset_rate);
        boolean isOffHand = hand.isOffHand();
        var animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());

        ((PlayerAttackAnimatable) player).playAttackAnimation(hand.attack().animation(), animatedHand, attackCooldownTicksFloat, upswingRate);
        SoundHelper.playSound(world, player, hand.attack().swingSound());
        setComboCount(getComboCount() + 1);
    }

    private AttackHand getCurrentHand() {
        return PlayerAttackHelper.getCurrentAttack(player, getComboCount());
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        lastAttacked += 1;
        cancelSwingIfNeeded();
        resetComboIfNeeded();
    }

    @Override
    public int getComboCount() {
        return ((PlayerAttackProperties)player).getComboCount();
    }

    private void setComboCount(int comboCount) {
        ((PlayerAttackProperties)player).setComboCount(comboCount);
    }

    private void cancelSwingIfNeeded() {
        if (upswingStack != null && !areItemStackEqual(player.getMainHandStack(), upswingStack)) {
            cancelWeaponSwing();
        }
    }

    private static boolean areItemStackEqual(ItemStack left, ItemStack right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return ItemStack.areEqual(left, right);
    }

    private void cancelWeaponSwing() {
        var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombat.config.upswing_multiplier));
        ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
    }
}
