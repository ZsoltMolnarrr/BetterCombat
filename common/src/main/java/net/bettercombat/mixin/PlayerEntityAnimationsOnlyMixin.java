package net.bettercombat.mixin;

import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.CombatMode;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.client.MinecraftClient;
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

    private PlayerEntity getPlayer() {
        // TODO: Fix this
        var player = (PlayerEntity)((Object)this);
        if (player == null) return null;
        return (PlayerEntity) MinecraftClient.getInstance().world.getEntityById(player.getId());
    }

    @Override
    protected void swingHand(CallbackInfo ci) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) return;
        var player = getPlayer();
        if (player == null) return;

        if (!playerShouldAttack(player)) {
            var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombat.config.upswing_multiplier));
            ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
            return;
        }

        var attackHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (attackHand == null) return;

        var attributes = WeaponRegistry.getAttributes(player.getMainHandStack());

        var animatedHand = AnimatedHand.from(attackHand.isOffHand(), attributes.isTwoHanded());
        var attackCooldownTicks = PlayerAttackHelper.getAttackCooldownTicksCapped(player);

        ((PlayerAttackAnimatable) player).playAttackAnimation(attackHand.attack().animation(), animatedHand, attackCooldownTicks, (float) attackHand.upswingRate());
        SoundHelper.playSound(MinecraftClient.getInstance().world, player, attackHand.attack().swingSound());

        ticksToResetCombo = Math.round(attackCooldownTicks * BetterCombat.config.combo_reset_rate);
        lastAttackedTicks = 0;
        ++comboCount;
        LOGGER.info("comboCount: " + comboCount);

        var playerMainHandStack = player.getMainHandStack();
        if (playerMainHandStack != null) itemLastAttackedWith = playerMainHandStack.getItem();
    }

    private boolean playerShouldAttack(PlayerEntity player) {
        var wherePlayerIsLooking = player.raycast(3, 1.0F, false);
        var entitiesInPlayerCrosshair = MinecraftClient.getInstance().world.getOtherEntities(null, new Box(player.getEyePos(), wherePlayerIsLooking.getPos()));
        if (entitiesInPlayerCrosshair != null && entitiesInPlayerCrosshair.size() > 1) {
            LOGGER.info("Player looking at entity");
            return true;
        }
        return wherePlayerIsLooking.getType() == HitResult.Type.MISS;
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.ANIMATIONS_ONLY) return;

        var player = getPlayer();
        if (player == null || comboCount <= 0) return;

        ++lastAttackedTicks;

        // Combo timeout
        if (lastAttackedTicks > ticksToResetCombo) {
            comboCount = 0;
            return;
        }

        // Switching weapon
        var playerMainHandStack = getPlayer().getMainHandStack();
        if (playerMainHandStack == null || playerMainHandStack.getItem() != itemLastAttackedWith) {
            comboCount = 0;
        }
    }
}
