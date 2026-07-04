package net.bettercombat.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.PlatformClient;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.logic.*;
import net.bettercombat.mixin.client.MinecraftClientAccessor;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.PatternMatching;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;

/**
 * Client-side attack management: upswing/combo state machine, attack-vs-mine input policy,
 * target caching, and attack packet/event dispatch.
 * Injected into the game by the thin `MinecraftClientInject` mixin, which delegates here.
 */
public class AttackInteractor {
    private final MinecraftClient client;

    public AttackInteractor(MinecraftClient client) {
        this.client = client;
    }

    private boolean isHoldingAttackInput = false;
    private boolean isHarvesting = false;

    private ItemStack upswingStack;
    private ItemStack lastAttacedWithItemStack;
    private WeaponSwing ongoingSwing;
    private int lastAttacked = 1000;
    private float lastSwingDuration = 0;
    private int comboReset = 0;

    private List<Entity> targetsInReach = null;

    // SECTION: Mixin entry points

    public void onDisconnected() {
        BetterCombatClientMod.ENABLED = false;
    }

    /**
     * Press to attack.
     * @return `true` if the vanilla attack should be cancelled
     */
    public boolean onDoAttack() {
        if (!BetterCombatClientMod.ENABLED) { return false; }

        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null && attributes.attacks() != null) {
            if (isTargetingMineableBlock() || isHarvesting) {
                isHarvesting = true;
                return false;
            }
            startUpswing(attributes);
            return true;
        }
        return false;
    }

    /**
     * Hold to attack.
     * @return `true` if the vanilla block breaking should be cancelled
     */
    public boolean onHandleBlockBreaking() {
        if (!BetterCombatClientMod.ENABLED) { return false; }

        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null && attributes.attacks() != null) {
            boolean cancel = false;
            boolean isPressed = client.options.attackKey.isPressed();
            if(isPressed && !isHoldingAttackInput) {
                if (isTargetingMineableBlock() || isHarvesting) {
                    isHarvesting = true;
                    return false;
                } else {
                    cancel = true;
                }
            }

            if (BetterCombatClientMod.config.isHoldToAttackEnabled && isPressed) {
                isHoldingAttackInput = true;
                startUpswing(attributes);
                cancel = true;
            } else {
                isHarvesting = false;
                isHoldingAttackInput = false;
            }
            return cancel;
        }
        return false;
    }

    /**
     * @return `true` if the vanilla item use should be cancelled
     */
    public boolean onDoItemUse() {
        if (!BetterCombatClientMod.ENABLED) { return false; }

        var hand = getCurrentHand();
        if (hand == null) { return false; }
        double upswingRate = hand.upswingRate();
        return currentUpswingTicks() > 0 || client.player.getAttackCooldownProgress(0) < (1.0 - upswingRate);
    }

    public void preTick() {
        var player = client.player;
        if (player == null) {
            return;
        }
        targetsInReach = null;
        lastAttacked += 1;

        if (ongoingSwing != null) {
            var time = currentTime();
            var swing = ongoingSwing; // Store in local for further checks
            if (swing.ticksLeft(time) <= 0) {
                ongoingSwing = null;
            }
            if (!player.isAlive() || !swing.isValid(time)) {
                cancelWeaponSwing();
            }
        }
        cancelSwingIfNeeded();
        attackFromUpswingIfNeeded();
        updateTargetsIfNeeded();
        resetComboIfNeeded();
    }

    public void postTick() {
        if (client.player == null) {
            return;
        }
        if (Keybindings.toggleMineKeyBinding.wasPressed()) {
            BetterCombatClientMod.config.isMiningWithWeaponsEnabled = !BetterCombatClientMod.config.isMiningWithWeaponsEnabled;
            AutoConfig.getConfigHolder(ClientConfigWrapper.class).save();

            var message = I18n.translate(BetterCombatClientMod.config.isMiningWithWeaponsEnabled ?
                    "hud.bettercombat.mine_with_weapons_on" : "hud.bettercombat.mine_with_weapons_off");
            client.inGameHud.setOverlayMessage(Text.literal(message), false);
        }
    }

    // SECTION: Attack logic

    private boolean isTargetingMineableBlock() {
        var player = client.player;
        if (!BetterCombatClientMod.config.isMiningWithWeaponsEnabled) {
            var whitelist = BetterCombatClientMod.config.mineWithWeaponWhitelist;
            if (whitelist == null || whitelist.isEmpty()) {
                return false;
            }
            var itemStack = player.getMainHandStack();
            var id = Registries.ITEM.getId(itemStack.getItem()).toString();
            if (!PatternMatching.matches(id, whitelist)) {
                return false;
            }
            // Weapon is whitelisted — fall through to continue checks
        }
        var regex = BetterCombatClientMod.config.mineWithWeaponBlacklist;
        if (regex != null && !regex.isEmpty()) {
            var itemStack = player.getMainHandStack();
            var id = Registries.ITEM.getId(itemStack.getItem()).toString();
            if (PatternMatching.matches(id, regex)) {
                return false;
            }
        }
        if (BetterCombatClientMod.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled
                && this.hasTargetsInReach()) {
            return false;
        }
        HitResult crosshairTarget = client.crosshairTarget;
        if (crosshairTarget != null && crosshairTarget.getType() == BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) crosshairTarget;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState clicked = client.world.getBlockState(pos);
            if (shouldSwingThruGrass()) {
                if (!clicked.getCollisionShape(client.world, pos).isEmpty() || clicked.getHardness(client.world, pos) != 0.0F) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean shouldSwingThruGrass() {
        if(!BetterCombatClientMod.config.isSwingThruGrassEnabled) {
            return false;
        }
        if (BetterCombatClientMod.config.isSwingThruGrassSmart
                && !this.hasTargetsInReach()) {
            return false;
        }
        var regex = BetterCombatClientMod.config.swingThruGrassBlacklist;
        if (regex == null || regex.isEmpty()) {
            return true;
        }
        var itemStack = client.player.getMainHandStack();
        var id = Registries.ITEM.getId(itemStack.getItem()).toString();
        return !PatternMatching.matches(id, regex);
    }

    private int currentTime() {
        if (client.player == null) {
            return 0;
        }
        return client.player.age;
    }

    private int currentUpswingTicks() {
        if (ongoingSwing == null) {
            return 0;
        }
        return ongoingSwing.upswingTicksLeft(currentTime());
    }

    private void startUpswing(WeaponAttributes attributes) {
        var player = client.player;

        // Guard conditions

        if (player.isRiding()) {
            // isRiding is `isHandsBusy()` according to official mappings
            // Support for revival mod
            return;
        }

        var attackHand = getCurrentHand();
        if (attackHand == null) { return; }
        float upswingRate = (float) attackHand.upswingRate();
        if (currentUpswingTicks() > 0
                || ((MinecraftClientAccessor) client).getAttackCooldown() > 0
                || player.isUsingItem()
                || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            return;
        }

        // Starting upswing
        player.stopUsingItem();

        lastAttacked = 0;
        upswingStack = player.getMainHandStack();
        float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(player); // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownLengthTicks`
        int attackCooldownTicks = Math.round(attackCooldownTicksFloat);
        this.comboReset = Math.round(attackCooldownTicksFloat * BetterCombatMod.config.combo_reset_rate);
        var upswingTicks = Math.max(Math.round(attackCooldownTicksFloat * upswingRate), 1); // At least 1 upswing ticks
        this.ongoingSwing = new WeaponSwing(attackHand, currentTime(), upswingTicks, attackCooldownTicksFloat);
        this.lastSwingDuration = attackCooldownTicksFloat;
        setItemUseCooldown(attackCooldownTicks); // Vanilla MinecraftClient property for compatibility
        setMiningCooldown(attackCooldownTicks);
        String animationName = attackHand.attack().animation();
        boolean isOffHand = attackHand.isOffHand();
        var animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());
        ((PlayerAttackAnimatable) player).playAttackAnimation(animationName, animatedHand, attackCooldownTicksFloat, upswingRate);

        var particles = SlashParticleUtil.trailParticlesFromAttack(attackHand);
        var appearance = SlashParticleUtil.appearanceFromItemStack(attackHand.itemStack());
        var packet = new Packets.AttackAnimation(
                player.getId(), animatedHand, animationName, attackCooldownTicksFloat, upswingRate,
                (float)PlayerAttackHelper.getStaticRange(player, attackHand.itemStack()),
                upswingTicks,
                new Packets.SwingParticles(particles, appearance)
        );
        Platform.networkC2S_Send(packet);
        BetterCombatClientEvents.ATTACK_START.invoke(handler -> {
            handler.onPlayerAttackStart(player, attackHand);
        });
    }

    private void cancelSwingIfNeeded() {
        if (upswingStack != null && !areItemStackEqual(client.player.getMainHandStack(), upswingStack)) {
            cancelWeaponSwing();
            return;
        }
    }

    private void attackFromUpswingIfNeeded() {
        if (ongoingSwing != null && currentUpswingTicks() == 0) {
            performAttack();
            upswingStack = null;
        }
    }

    private void resetComboIfNeeded() {
        var player = client.player;
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

    private boolean shouldUpdateTargetsInReach() {
        if(BetterCombatClientMod.config.isHighlightCrosshairEnabled
                || BetterCombatClientMod.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled) {
            return targetsInReach == null;
        }
        return false;
    }

    private void updateTargetsInReach(List<Entity> targets) {
        targetsInReach = targets;
    }

    private void updateTargetsIfNeeded() {
        if (shouldUpdateTargetsInReach()) {
            var player = client.player;
            List<Entity> targets = List.of();
            var hand = PlayerAttackHelper.getCurrentAttack(player, getComboCount());
            if (hand != null) {
                WeaponAttributes attributes = WeaponRegistry.getAttributes(hand.itemStack());
                var range = PlayerAttackHelper.getRangeForItem(player, hand.itemStack());
                range *= hand.attack().rangeMultiplier();
                if (attributes != null && attributes.attacks() != null) {
                    targets = TargetFinder.findAttackTargets(
                            player,
                            getCursorTarget(),
                            hand.attack(),
                            range);
                }
            }
            updateTargetsInReach(targets);
        }
    }

    private void performAttack() {
        var player = client.player;
        if (Keybindings.feintKeyBinding.isPressed()) {
            player.resetTicksSinceLastAttack();
            cancelWeaponSwing();
            return;
        }

        var weaponSwing = this.ongoingSwing;
        if (weaponSwing == null) {
            return;
        }
        var hand = weaponSwing.attackHand();
        if (hand == null) { return; }
        var attack = hand.attack();
        var upswingRate = hand.upswingRate();
        if (player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            return;
        }

        var cursorTarget = getCursorTarget();
        var range = PlayerAttackHelper.getRangeForItem(player, hand.itemStack());
        range *= hand.attack().rangeMultiplier();
        List<Entity> targets = TargetFinder.findAttackTargets(
                player,
                cursorTarget,
                attack,
                range);
        updateTargetsInReach(targets);
        if(targets.size() == 0) {
            PlatformClient.onEmptyLeftClick(player);

            if (client.crosshairTarget.getType() == BLOCK) {
                var blockHitResult = (BlockHitResult) client.crosshairTarget;
                var pos = blockHitResult.getBlockPos();
                var packet = new Packets.C2S_BlockHit(pos);
                Platform.networkC2S_Send(packet);
            }
        }

        // Mimic logic of:
        // ClientPlayerInteractionManager.attackEntity(PlayerEntity player, Entity target)
        var packet = new Packets.C2S_AttackRequest(getComboCount(), player.isSneaking(), player.getInventory().getSelectedSlot(), cursorTarget, targets);
        Platform.networkC2S_Send(packet);
        for (var target: targets) {
            player.attack(target);
        }
        player.resetTicksSinceLastAttack();
        BetterCombatClientEvents.ATTACK_HIT.invoke(handler -> {
            handler.onPlayerAttackStart(player, hand, targets, cursorTarget);
        });

        var particles = SlashParticleUtil.trailParticlesFromAttack(hand);
        var appearance = SlashParticleUtil.appearanceFromItemStack(hand.itemStack());
        SlashParticleUtil.spawnParticles(player, hand.isOffHand(), (float)PlayerAttackHelper.getStaticRange(player, hand.itemStack()), particles, appearance);

        setComboCount(getComboCount() + 1);
        if (!hand.isOffHand()) {
            lastAttacedWithItemStack = hand.itemStack();
        }
    }

    private Entity getCursorTarget() {
        return ((MinecraftClient_BetterCombat) client).getCursorTarget();
    }

    private AttackHand getCurrentHand() {
        return PlayerAttackHelper.getCurrentAttack(client.player, getComboCount());
    }

    private void setComboCount(int comboCount) {
        ((PlayerAttackProperties)client.player).setComboCount(comboCount);
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

    private void setItemUseCooldown(int ticks) {
        ((MinecraftClientAccessor) client).setItemUseCooldown(ticks);
    }

    private void setMiningCooldown(int ticks) {
        ((MinecraftClientAccessor) client).setAttackCooldown(ticks); // This is actually the mining cooldown
    }

    private void cancelWeaponSwing() {
        var player = client.player;
        var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombatMod.config.upswing_multiplier));
        ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
        var packet = Packets.AttackAnimation.stop(player.getId(), downWind);
        Platform.networkC2S_Send(packet);
        upswingStack = null;
        ongoingSwing = null;
        setItemUseCooldown(0);
        setMiningCooldown(0);
    }

    // SECTION: MinecraftClient_BetterCombat delegates

    public int getComboCount() {
        return ((PlayerAttackProperties)client.player).getComboCount();
    }

    public boolean hasTargetsInReach() {
        return targetsInReach != null && !targetsInReach.isEmpty();
    }

    public float getSwingProgress() {
        if (lastAttacked > lastSwingDuration || lastSwingDuration <= 0) {
            return 1F;
        }
        return (float)lastAttacked / lastSwingDuration;
    }

    public int getUpswingTicks() {
        return currentUpswingTicks();
    }

    public void cancelUpswing() {
        if (currentUpswingTicks() > 0) {
            cancelWeaponSwing();
        }
    }

    public AttackHand getCurrentAttackHand() {
        if (this.ongoingSwing != null) {
            return this.ongoingSwing.attackHand();
        }
        return null;
    }
}
