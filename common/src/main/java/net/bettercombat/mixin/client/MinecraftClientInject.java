package net.bettercombat.mixin.client;

import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.AttackInteractor;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Thin injection glue. All attack management logic lives in `AttackInteractor`.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientInject implements MinecraftClient_BetterCombat {
    @Unique
    private AttackInteractor interactor;

    @Unique
    private AttackInteractor interactor() {
        if (interactor == null) {
            interactor = new AttackInteractor((MinecraftClient)(Object)this);
        }
        return interactor;
    }

    // Targeting the method where all the disconnection related logic is.
    @Inject(method = "onDisconnected", at = @At("TAIL"))
    private void disconnect_TAIL(CallbackInfo ci) {
        interactor().onDisconnected();
    }

    // Press to attack
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        if (interactor().onDoAttack()) {
            info.setReturnValue(false);
        }
    }

    // Hold to attack
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
        if (interactor().onHandleBlockBreaking()) {
            ci.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void pre_doItemUse(CallbackInfo ci) {
        if (interactor().onDoItemUse()) {
            ci.cancel();
        }
    }

    // HEAD: must run before vanilla decrements `attackCooldown`/`itemUseCooldown` and processes input for this tick
    @Inject(method = "tick", at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        interactor().preTick();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void post_Tick(CallbackInfo ci) {
        interactor().postTick();
    }

    // SECTION: MinecraftClient_BetterCombat

    @Override
    public int getComboCount() {
        return interactor().getComboCount();
    }

    @Override
    public boolean hasTargetsInReach() {
        return interactor().hasTargetsInReach();
    }

    @Override
    public float getSwingProgress() {
        return interactor().getSwingProgress();
    }

    @Override
    public int getUpswingTicks() {
        return interactor().getUpswingTicks();
    }

    @Override
    public void cancelUpswing() {
        interactor().cancelUpswing();
    }

    @Override
    public AttackHand getCurrentAttackHand() {
        return interactor().getCurrentAttackHand();
    }
}
