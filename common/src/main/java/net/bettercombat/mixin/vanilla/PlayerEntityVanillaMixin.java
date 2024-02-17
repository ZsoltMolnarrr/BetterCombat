package net.bettercombat.mixin.vanilla;

import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.CombatMode;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityVanillaMixin extends LivingEntityVanillaMixin {
    @Unique private int comboCount = 0;
    @Unique private int ticksToResetCombo = 0;
    @Unique private int lastAttackedTicks = 0;
    @Unique private Item itemLastAttackedWith = null;

    @Shadow public abstract void attack(Entity target);

    @Unique
    private PlayerEntity getPlayer() {
        var clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return null;
        return (PlayerEntity)clientWorld.getEntityById(getEntity().getId());
    }

    @Unique
    private Entity getEntity() {
        return (Entity)((Object)this);
    }

    @Override
    protected void swingHand(CallbackInfo ci) {
        if (!getEntity().getWorld().isClient() || BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) {
            return;
        }

        var player = getPlayer();
        if (player == null) return;

        if (player.getAttackCooldownProgress(0) > 0) {
            comboCount = 0;
            var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombat.config.upswing_multiplier));
            ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
            return;
        }

        var attack = PlayerAttackHelper.getCurrentAttackAnimationOnly(player, comboCount);
        if (attack == null) return;
        var attackCooldownTicks = PlayerAttackHelper.getAttackCooldownTicksCapped(player);

        ((PlayerAttackAnimatable) player).playAttackAnimation(attack.animation(), AnimatedHand.MAIN_HAND, attackCooldownTicks, (float) attack.upswingRate());
        SoundHelper.playSound(player, attack.swingSound());

        ticksToResetCombo = Math.round(attackCooldownTicks * BetterCombat.config.combo_reset_rate);
        lastAttackedTicks = 0;
        ++comboCount;

        var playerMainHandStack = player.getMainHandStack();
        if (playerMainHandStack != null) itemLastAttackedWith = playerMainHandStack.getItem();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        if (!getEntity().getWorld().isClient() || BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) {
            return;
        }

        if (lastAttackedTicks <= 500) ++lastAttackedTicks;

        // Combo timeout
        if (lastAttackedTicks > ticksToResetCombo) {
            comboCount = 0;
            return;
        }

        var player = getPlayer();
        if (player == null || comboCount <= 0) return;

        // Switching weapon
        var playerMainHandStack = player.getMainHandStack();
        if (playerMainHandStack == null || playerMainHandStack.getItem() != itemLastAttackedWith) {
            comboCount = 0;
        }
    }
}
