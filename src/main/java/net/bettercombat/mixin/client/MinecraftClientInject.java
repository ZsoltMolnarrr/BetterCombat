package net.bettercombat.mixin.client;

import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.MinecraftClientExtension;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientInject implements MinecraftClientExtension {
    @Shadow public ClientWorld world;
    @Shadow @Nullable public ClientPlayerEntity player;
    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }
    private boolean isHoldingAttackInput = false;
    private boolean hasTargetsInRange = false;

    // Press to attack
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null) {
            if (isTargetingMineableBlock()) {
                return;
            }
            startUpswing(attributes);
            info.setReturnValue(false);
            info.cancel();
        }
    }

    // Hold to attack
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null) {
            boolean isPressed = client.options.attackKey.isPressed();
            if(isPressed && !isHoldingAttackInput) {
                if (isTargetingMineableBlock()) {
                    return;
                } else {
                    ci.cancel();
                }
            }

            if (BetterCombatClient.config.isHoldToAttackEnabled && isPressed) {
                isHoldingAttackInput = true;
                startUpswing(attributes);
                ci.cancel();
            } else {
                isHoldingAttackInput = false;
            }
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void pre_doItemUse(CallbackInfo ci) {
        var hand = getCurrentHand();
        if (hand == null) { return; }
        double upswingRate = hand.upswingRate();
        if (upswingTicks > 0 || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            ci.cancel();
        }
    }


    private boolean isTargetingMineableBlock() {
        if (!BetterCombatClient.config.isMiningWithWeaponsEnabled) {
            return false;
        }
        MinecraftClient client = thisClient();
        HitResult crosshairTarget = client.crosshairTarget;
        if (crosshairTarget != null && crosshairTarget.getType() == BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) crosshairTarget;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState clicked = world.getBlockState(pos);
            if (BetterCombatClient.config.isSwingThruGrassEnabled) {
                if (!clicked.getCollisionShape(world, pos).isEmpty() || clicked.getHardness(world, pos) != 0.0F) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private static float ComboResetRate = 3F;

    private ItemStack upswingStack;
    private ItemStack lastAttacedWithItemStack;
    private int upswingTicks = 0;
    private int lastAttacked = 1000;

    private void startUpswing(WeaponAttributes attributes) {
        // Guard conditions

        var hand = getCurrentHand();
        if (hand == null) { return; }
        double upswingRate = hand.upswingRate();
        if (upswingTicks > 0 || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
//            double attackCooldownTicks = player.getAttackCooldownProgressPerTick() / PlayerAttackHelper.getDualWieldingAttackSpeedMultiplier(player);
//            var currentCD = Math.round(attackCooldownTicks * player.getAttackCooldownProgress(0));
//            System.out.println("Waiting for cooldown: " + currentCD + "/" + attackCooldownTicks);
            return;
        }

        // Starting upswing
        player.setBodyYaw(player.getHeadYaw()); // Restore auto body rotation upon swing - Fix issue #11
        player.stopUsingItem();
        lastAttacked = 0;
        upswingStack = player.getMainHandStack();
        float attackCooldownTicks = PlayerAttackHelper.getScaledAttackCooldown(player);
        this.upswingTicks = (int)(Math.round(attackCooldownTicks * upswingRate));
//        System.out.println("Starting upswingTicks: " + upswingTicks);
        String animationName = hand.attack().animation();
        boolean isOffHand = hand.isOffHand();
        ((PlayerAttackAnimatable) player).playAttackAnimation(animationName, isOffHand, attackCooldownTicks);
        ClientPlayNetworking.send(
                Packets.AttackAnimation.ID,
                Packets.AttackAnimation.writePlay(player.getId(), isOffHand, animationName, attackCooldownTicks));
    }

    private void feintIfNeeded() {
        if (upswingTicks > 0 &&
                (BetterCombatClient.feintKeyBinding.isPressed() || player.getMainHandStack() != upswingStack)) {
            ((PlayerAttackAnimatable) player).stopAttackAnimation();
            ClientPlayNetworking.send(
                    Packets.AttackAnimation.ID,
                    Packets.AttackAnimation.writeStop(player.getId()));
            upswingTicks = 0;
            upswingStack = null;
        }
    }

    private void attackFromUpswingIfNeeded() {
        if (upswingTicks > 0) {
            --upswingTicks;
            if (upswingTicks == 0) {
                performAttack();
                upswingStack = null;
            }
        }
    }

    private void resetComboIfNeeded() {
        double attackCooldownTicks = player.getAttackCooldownProgressPerTick();
        int comboReset = (int)Math.round(attackCooldownTicks * ComboResetRate);
        // Combo timeout
        if(lastAttacked > comboReset && getComboCount() > 0) {
            setComboCount(0);
        }
        // Switching main-hand weapon
        if (!PlayerAttackHelper.shouldAttackWithOffHand(player, getComboCount())) {
            if(player.getMainHandStack() == null
                    || (lastAttacedWithItemStack != null && !lastAttacedWithItemStack.getItem().equals(player.getMainHandStack().getItem()) ) ) {
                setComboCount(0);
            }
        }
    }

    private boolean ranTargetCheckCurrentTick = false;

    @Inject(method = "tick",at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        ranTargetCheckCurrentTick = false;
        lastAttacked += 1;
        feintIfNeeded();
        attackFromUpswingIfNeeded();
        resetComboIfNeeded();
    }

    @Inject(method = "tick",at = @At("TAIL"))
    private void post_Tick(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        if ((BetterCombatClient.config.isHighlightCrosshairEnabled)
                && !ranTargetCheckCurrentTick) {
            MinecraftClient client = thisClient();
            var hand = PlayerAttackHelper.getCurrentAttack(player, getComboCount());
            WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
            List<Entity> targets = List.of();
            if (attributes != null) {
                targets = TargetFinder.findAttackTargets(
                    player,
                    getCursorTarget(),
                    hand.attack(),
                    attributes.attackRange());
            }
            updateTargetsInRage(targets);
        }
    }

    private void performAttack() {
        MinecraftClient client = thisClient();
        var hand = getCurrentHand();
        if (hand == null) { return; }
        var attack = hand.attack();
        var upswingRate = hand.upswingRate();
        if (client.player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            return;
        }
        // System.out.println("Attack with CD: " + client.player.getAttackCooldownProgress(0));
        List<Entity> targets = TargetFinder.findAttackTargets(
                player,
                getCursorTarget(),
                attack,
                hand.attributes().attackRange());
        updateTargetsInRage(targets);
        ClientPlayNetworking.send(
                Packets.C2S_AttackRequest.ID,
                Packets.C2S_AttackRequest.write(getComboCount(), player.isSneaking(), targets));
        client.player.resetLastAttackedTicks();
        ((MinecraftClientAccessor) client).setAttackCooldown(10); // This is actually the mining cooldown
        setComboCount(getComboCount() + 1);
        if (!hand.isOffHand()) {
            lastAttacedWithItemStack = hand.itemStack();
        }
    }

    private AttackHand getCurrentHand() {
        return PlayerAttackHelper.getCurrentAttack(player, getComboCount());
    }

    private void updateTargetsInRage(List<Entity> targets) {
        hasTargetsInRange = targets.size() > 0;
        ranTargetCheckCurrentTick = true;
    }

    private void setComboCount(int comboCount) {
        ((PlayerAttackProperties)player).setComboCount(comboCount);
    }

    // MinecraftClientExtension

    @Override
    public int getComboCount() {
        return ((PlayerAttackProperties)player).getComboCount();
    }

    @Override
    public boolean hasTargetsInRange() {
        return hasTargetsInRange;
    }
}
