package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.MeleeWeaponAttributes;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.MinecraftClientHelper;
import net.bettercombat.client.PlayerExtension;
import net.bettercombat.client.collision.TargetFinder;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
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
import static net.minecraft.util.hit.HitResult.Type.ENTITY;

@Mixin(MinecraftClient.class)
public class MinecraftClientInject {
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
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);
            if (attributes != null) {
                if (isTargetingMineableBlock()) {
                    return;
                }
                startUpswing();
                info.setReturnValue(false);
                info.cancel();
            }
        }
    }

    // Hold to attack
    @Inject(method = "handleBlockBreaking",at = @At("HEAD"), cancellable = true)
    private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
        MinecraftClient client = thisClient();
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);

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

    private static float UpswingRate = 0.25F; // TODO: Move this constant to config or attributes?

    private int upswingTicks = 0;

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
        this.upswingTicks = getUpswingLength(player);
        ((PlayerExtension) player).animate("slash");
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void post_Tick(CallbackInfo ci) {
        if (upswingTicks > 0) {
            --upswingTicks;
            if (upswingTicks == 0) {
                performAttack();
            }
        }
    }

    private boolean performAttack() {
        MinecraftClient client = thisClient();
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);
            if (attributes != null) {
                if (client.player.getAttackCooldownProgress(0) < (1.0 - UpswingRate)) {
                    return true;
                }

                client.player.resetLastAttackedTicks();

                List<Entity> targets = TargetFinder.findAttackTargets(player, MinecraftClientHelper.getCursorTarget(client), attributes);
                for (Entity target : targets) {
                    client.interactionManager.attackEntity(player, target);
                }
                ((MinecraftClientAccessor) client).setAttackCooldown(10);
                return true;
            }
        }
        return false;
    }
}
