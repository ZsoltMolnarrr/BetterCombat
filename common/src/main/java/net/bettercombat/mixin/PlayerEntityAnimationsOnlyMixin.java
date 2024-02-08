package net.bettercombat.mixin;

import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.CombatMode;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAnimationsOnlyMixin extends LivingEntityAnimationsOnlyMixin {
    private final Logger LOGGER = LogUtils.getLogger();

    @Unique private int comboCount = 0;

    @Unique private int ticksToResetCombo = 0;

    @Unique private int lastAttackedTicks = 0;

    @Unique private Item itemLastAttackedWith = null;

    @Unique
    private PlayerEntity getPlayer(ClientWorld world) {
        var player = (PlayerEntity)((Object)this);
        if (player == null) return null;
        return (PlayerEntity) world.getEntityById(player.getId());
    }

    @Override
    protected void swingHand(CallbackInfo ci) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) return;

        var clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return;
        var player = getPlayer(clientWorld);
        if (player == null) return;

        if (isPlayerMining(player)) {
            var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombat.config.upswing_multiplier));
            ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
            return;
        }

        var attackHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (attackHand == null) return;

        var attackCooldownTicks = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
        ((PlayerAttackAnimatable) player).playAttackAnimation(attackHand.attack().animation(), AnimatedHand.MAIN_HAND, attackCooldownTicks, (float) attackHand.upswingRate());
        SoundHelper.playSound(clientWorld, player, attackHand.attack().swingSound());

        ticksToResetCombo = Math.round(attackCooldownTicks * BetterCombat.config.combo_reset_rate);
        lastAttackedTicks = 0;
        ++comboCount;

        var playerMainHandStack = player.getMainHandStack();
        if (playerMainHandStack != null) itemLastAttackedWith = playerMainHandStack.getItem();
    }

    @Unique
    private static double getPlayerBuildReach(PlayerEntity player) {
        if (player.isCreative()) return 5;
        return 4.5;
    }

    @Unique
    private boolean isPlayerMining(PlayerEntity player) {
        var playerCrosshairTarget = player.raycast(getPlayerBuildReach(player), 1.0F, false);
        var entitiesInPlayerCrosshair = player.getWorld().getOtherEntities(null, new Box(player.getEyePos(), playerCrosshairTarget.getPos()));
        if (entitiesInPlayerCrosshair.size() > 1) return true;
        return playerCrosshairTarget.getType() == HitResult.Type.BLOCK;
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) return;

        var clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return;
        var player = getPlayer(clientWorld);
        if (player == null || comboCount <= 0) return;

        ++lastAttackedTicks;

        // Combo timeout
        if (lastAttackedTicks > ticksToResetCombo) {
            comboCount = 0;
            return;
        }

        // Switching weapon
        var playerMainHandStack = player.getMainHandStack();
        if (playerMainHandStack == null || playerMainHandStack.getItem() != itemLastAttackedWith) {
            comboCount = 0;
        }
    }
}
