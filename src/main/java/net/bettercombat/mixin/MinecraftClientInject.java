package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.MinecraftClientExtension;
import net.bettercombat.client.MinecraftClientHelper;
import net.bettercombat.client.PlayerExtension;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.network.WeaponSwingPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
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
import java.util.stream.Collectors;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;

@Mixin(MinecraftClient.class)
public class MinecraftClientInject implements MinecraftClientExtension {
    @Shadow public ClientWorld world;
    @Shadow @Nullable public ClientPlayerEntity player;
    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }
    private boolean isHoldingAttack = false;

    // Press to attack
    @Inject(method = "doAttack",at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null) {
            if (isTargetingMineableBlock()) {
                return;
            }
            startUpswing();
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
            if(isPressed && !isHoldingAttack) {
                if (isTargetingMineableBlock()) {
                    return;
                } else {
                    ci.cancel();
                }
            }

            if (BetterCombatClient.config.isHoldToAttackEnabled && isPressed) {
                isHoldingAttack = true;
                startUpswing();
                ci.cancel();
            } else {
                isHoldingAttack = false;
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

    private static float UpswingRate = 0.25F; // TODO: Move this constant to config
    private static float ComboResetRate = 3F; // TODO: Move this constant to config

    private int upswingTicks = 0;
    private int comboCount = 0;
    private int lastAttacked = 1000;

    private int getUpswingLength(PlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        Identifier id = Registry.ITEM.getId(item);
        double attackCooldownTicks = 20.0 / player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
        return (int)(Math.round(attackCooldownTicks * UpswingRate));
    }

    private void startUpswing() {
        if (upswingTicks > 0 || player.getAttackCooldownProgress(0) < (1.0 - UpswingRate)) {
            return;
        }
        lastAttacked = 0;
        this.upswingTicks = getUpswingLength(player);
        ((PlayerExtension) player).animate("slash");
    }

    private void feintIfNeeded() {
        if (BetterCombatClient.feintKeyBinding.isPressed()
                && upswingTicks > 0) {
            ((PlayerExtension) player).stopAnimation();
            upswingTicks = 0;
        }
    }

    private void attackFromUpswingIfNeeded() {
        if (upswingTicks > 0) {
            --upswingTicks;
            if (upswingTicks == 0) {
                performAttack();
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

    @Inject(method = "tick",at = @At("HEAD"))
    private void post_Tick(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        lastAttacked += 1;
        feintIfNeeded();
        attackFromUpswingIfNeeded();
        resetComboIfNeeded();
    }

    private boolean performAttack() {
        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null) {
            if (client.player.getAttackCooldownProgress(0) < (1.0 - UpswingRate)) {
                return true;
            }

            List<Entity> targets = TargetFinder.findAttackTargets(
                    player,
                    MinecraftClientHelper.getCursorTarget(client),
                    attributes.currentAttack(comboCount),
                    attributes.attackRange());
            PacketByteBuf buffer = PacketByteBufs.create();
            ClientPlayNetworking.send(
                    WeaponSwingPacket.C2S_AttackRequest.ID,
                    WeaponSwingPacket.C2S_AttackRequest.write(buffer, comboCount, true, player.isSneaking(), targets));
//            for (Entity target : targets) {
//                client.interactionManager.attackEntity(player, target);
//            }
            client.player.resetLastAttackedTicks();
            ((MinecraftClientAccessor) client).setAttackCooldown(10);
            comboCount += 1;
            return true;
        }
        return false;
    }

    // MinecraftClientExtension

    @Override
    public int getComboCount() {
        return comboCount;
    }
}
