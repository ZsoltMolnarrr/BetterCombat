package net.bettercombat.mixin.client;

import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.*;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class AnimationsOnlyMixin {
    private final Logger LOGGER = LogUtils.getLogger();

    private int lastAttacked = 1000;

    private ItemStack upswingStack;

    private int comboReset = 0;

    private int upswingTicks = 0;

    @Inject(method = "swingHand", at = @At("HEAD"))
    private void tickMovement_ModifyInput(CallbackInfo ci) {
        LOGGER.info("Animate here");
        if (BetterCombat.getCurrentCombatMode() == CombatMode.BETTER_COMBAT) return;

        // TODO: Fix this
        var player = (ServerPlayerEntity)((Object)this);
        var entity = (PlayerEntity) MinecraftClient.getInstance().world.getEntityById(player.getId());
        WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandStack());

        startUpswing(entity, attributes);
    }

    private void startUpswing(PlayerEntity player, WeaponAttributes attributes) {
        var hand = getCurrentHand(player);
        if (hand == null) { return; }
        float upswingRate = (float) hand.upswingRate();

        lastAttacked = 0;
        upswingStack = player.getMainHandStack();
        float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(player); // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownLengthTicks`
        this.comboReset = Math.round(attackCooldownTicksFloat * BetterCombat.config.combo_reset_rate);
        this.upswingTicks = Math.max(Math.round(attackCooldownTicksFloat * upswingRate), 1); // At least 1 upswing ticks
        boolean isOffHand = hand.isOffHand();
        var animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());

        ((PlayerAttackAnimatable) player).playAttackAnimation(hand.attack().animation(), animatedHand, attackCooldownTicksFloat, upswingRate);
        SoundHelper.playSound(MinecraftClient.getInstance().world, player, hand.attack().swingSound());
        setComboCount(player, getComboCount(player) + 1);
    }

    private AttackHand getCurrentHand(PlayerEntity player) {
        return PlayerAttackHelper.getCurrentAttack(player, getComboCount(player));
    }

    public int getComboCount(PlayerEntity player) {
        return ((PlayerAttackProperties)player).getComboCount();
    }

    private void setComboCount(PlayerEntity player, int comboCount) {
        ((PlayerAttackProperties)player).setComboCount(comboCount);
    }
}
