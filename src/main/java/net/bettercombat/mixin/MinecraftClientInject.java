package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.attack.AttackHand;
import net.bettercombat.attack.PlayerAttackHelper;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.MinecraftClientExtension;
import net.bettercombat.client.PlayerAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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
public class MinecraftClientInject implements MinecraftClientExtension {
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
    @Inject(method = "handleBlockBreaking",at = @At("HEAD"), cancellable = true)
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
    private int upswingTicks = 0;
    private int comboCount = 0;
    private int lastAttacked = 1000;

    private int getUpswingLength(PlayerEntity player, double upswingRate) {
        Item item = player.getMainHandStack().getItem();
        Identifier id = Registry.ITEM.getId(item);
        double attackCooldownTicks = 20.0 / player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
        return (int)(Math.round(attackCooldownTicks * upswingRate));
    }

    private void startUpswing(WeaponAttributes attributes) {
        var hand = getCurrentHand();
        if (hand == null) { return; }
        double upswingRate = hand.upswingRate();
        if (upswingTicks > 0 || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            return;
        }
        lastAttacked = 0;
        upswingStack = player.getMainHandStack();
        this.upswingTicks = getUpswingLength(player, upswingRate);

        String animationName = hand.attack().animation();
        boolean isOffHand = hand.isOffHand();
        ((PlayerAnimatable) player).playAttackAnimation(animationName, isOffHand);
        ClientPlayNetworking.send(
                Packets.AttackAnimation.ID,
                Packets.AttackAnimation.writePlay(player.getId(), isOffHand, animationName));
    }

    private void feintIfNeeded() {
        if (upswingTicks > 0 &&
                (BetterCombatClient.feintKeyBinding.isPressed() || player.getMainHandStack() != upswingStack)) {
            ((PlayerAnimatable) player).stopAnimation();
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
        double attackCooldownTicks = 20.0 / player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
        int comboReset = (int)Math.round(attackCooldownTicks * ComboResetRate);
        if(lastAttacked > comboReset && comboCount > 0) {
            comboCount = 0;
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
        if ((BetterCombatClient.config.isHighlightAttackIndicatorEnabled || BetterCombatClient.config.isHighlightCrosshairEnabled)
                && !ranTargetCheckCurrentTick) {
            MinecraftClient client = thisClient();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
            if (attributes != null) {
                List<Entity> targets = TargetFinder.findAttackTargets(
                    player,
                    getCursorTarget(),
                    attributes.currentAttack(comboCount),
                    attributes.attackRange());
                updateTargetsInRage(targets);
            }
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

        List<Entity> targets = TargetFinder.findAttackTargets(
                player,
                getCursorTarget(),
                attack,
                hand.attributes().attackRange());
        updateTargetsInRage(targets);
        ClientPlayNetworking.send(
                Packets.C2S_AttackRequest.ID,
                Packets.C2S_AttackRequest.write(comboCount, player.isSneaking(), targets));
        client.player.resetLastAttackedTicks();
        ((MinecraftClientAccessor) client).setAttackCooldown(10);
        comboCount += 1;
    }

    private AttackHand getCurrentHand() {
        return PlayerAttackHelper.getCurrentAttack(player, getComboCount());
    }

    private void updateTargetsInRage(List<Entity> targets) {
        hasTargetsInRange = targets.size() > 0;
        ranTargetCheckCurrentTick = true;
    }

    // MinecraftClientExtension

    @Override
    public int getComboCount() {
        return comboCount;
    }

    @Override
    public boolean hasTargetsInRange() {
        return hasTargetsInRange;
    }
}
