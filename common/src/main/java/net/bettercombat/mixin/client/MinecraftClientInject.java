package net.bettercombat.mixin.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.PlatformClient;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.Keybindings;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.logic.*;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.PatternMatching;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientInject implements MinecraftClient_BetterCombat {
    @Shadow public ClientWorld world;
    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow private int itemUseCooldown;

    @Shadow @Final public TextRenderer textRenderer;

    @Shadow public int attackCooldown;

    @Shadow @Final public InGameHud inGameHud;

    @Shadow @Nullable public HitResult crosshairTarget;

    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }
    private boolean isHoldingAttackInput = false;
    private boolean isHarvesting = false;

    // Targeting the method where all the disconnection related logic is.
    @Inject(method = "onDisconnected",at = @At("TAIL"))
    private void disconnect_TAIL(CallbackInfo ci) {
        BetterCombatClientMod.ENABLED = false;
    }

    // Press to attack
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        if (!BetterCombatClientMod.ENABLED) { return; }

        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null && attributes.attacks() != null) {
            if (isTargetingMineableBlock() || isHarvesting) {
                isHarvesting = true;
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
        if (!BetterCombatClientMod.ENABLED) { return; }

        MinecraftClient client = thisClient();
        WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
        if (attributes != null && attributes.attacks() != null) {
            boolean isPressed = client.options.attackKey.isPressed();
            if(isPressed && !isHoldingAttackInput) {
                if (isTargetingMineableBlock() || isHarvesting) {
                    isHarvesting = true;
                    return;
                } else {
                    ci.cancel();
                }
            }

            if (BetterCombatClientMod.config.isHoldToAttackEnabled && isPressed) {
                isHoldingAttackInput = true;
                startUpswing(attributes);
                ci.cancel();
            } else {
                isHarvesting = false;
                isHoldingAttackInput = false;
            }
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void pre_doItemUse(CallbackInfo ci) {
        if (!BetterCombatClientMod.ENABLED) { return; }

        var hand = getCurrentHand();
        if (hand == null) { return; }
        double upswingRate = hand.upswingRate();
        if (currentUpswingTicks() > 0 || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            ci.cancel();
        }
    }

    private boolean isTargetingMineableBlock() {
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
        MinecraftClient client = thisClient();
        HitResult crosshairTarget = client.crosshairTarget;
        if (crosshairTarget != null && crosshairTarget.getType() == BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) crosshairTarget;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState clicked = world.getBlockState(pos);
            if (shouldSwingThruGrass()) {
                if (!clicked.getCollisionShape(world, pos).isEmpty() || clicked.getHardness(world, pos) != 0.0F) {
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
        var itemStack = player.getMainHandStack();
        var id = Registries.ITEM.getId(itemStack.getItem()).toString();
        return !PatternMatching.matches(id, regex);
    }

    private ItemStack upswingStack;
    private ItemStack lastAttacedWithItemStack;
    private WeaponSwing ongoingSwing;
//    private int upswingTicks = 0;
    private int lastAttacked = 1000;
    private float lastSwingDuration = 0;
    private int comboReset = 0;

    @Unique private int currentTime() {
        if (player == null) {
            return 0;
        }
        return player.age;
    }
    @Unique private int currentUpswingTicks() {
        if (ongoingSwing == null) {
            return 0;
        }
        return ongoingSwing.upswingTicksLeft(currentTime());
    }

    private void startUpswing(WeaponAttributes attributes) {
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
                || attackCooldown > 0
                || player.isUsingItem()
                || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
//            double attackCooldownTicks = PlayerAttackHelper.getAttackCooldownTicksCapped(player) / PlayerAttackHelper.getDualWieldingAttackSpeedMultiplier(player);
//            var currentCD = Math.round(attackCooldownTicks * player.getAttackCooldownProgress(0));
//            System.out.println("Waiting for cooldown: " + currentCD + "/" + attackCooldownTicks);
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
        this.itemUseCooldown = attackCooldownTicks; // Vanilla MinecraftClient property for compatibility
        setMiningCooldown(attackCooldownTicks);
//        System.out.println("Starting upswingTicks: " + upswingTicks);
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
        if (upswingStack != null && !areItemStackEqual(player.getMainHandStack(), upswingStack)) {
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

    private List<Entity> targetsInReach = null;

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

    @Inject(method = "tick",at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
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

    @Inject(method = "tick",at = @At("TAIL"))
    private void post_Tick(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        if (Keybindings.toggleMineKeyBinding.wasPressed()) {
            BetterCombatClientMod.config.isMiningWithWeaponsEnabled = !BetterCombatClientMod.config.isMiningWithWeaponsEnabled;
            AutoConfig.getConfigHolder(ClientConfigWrapper.class).save();

            var message = I18n.translate(BetterCombatClientMod.config.isMiningWithWeaponsEnabled ?
                    "hud.bettercombat.mine_with_weapons_on" : "hud.bettercombat.mine_with_weapons_off");
            inGameHud.setOverlayMessage(Text.literal(message), false);
        }
    }

    private void performAttack() {
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
        // System.out.println("Attack with CD: " + client.player.getAttackCooldownProgress(0));

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

            if (crosshairTarget.getType() == BLOCK) {
                var blockHitResult = (BlockHitResult) crosshairTarget;
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

    private AttackHand getCurrentHand() {
        return PlayerAttackHelper.getCurrentAttack(player, getComboCount());
    }

    private void setComboCount(int comboCount) {
        ((PlayerAttackProperties)player).setComboCount(comboCount);
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

    private void setMiningCooldown(int ticks) {
        MinecraftClient client = thisClient();
        ((MinecraftClientAccessor) client).setAttackCooldown(ticks); // This is actually the mining cooldown
    }

    private void cancelWeaponSwing() {
        var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombatMod.config.upswing_multiplier));
        ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
        var packet = Packets.AttackAnimation.stop(player.getId(), downWind);
        Platform.networkC2S_Send(packet);
        upswingStack = null;
        ongoingSwing = null;
        itemUseCooldown = 0;
        setMiningCooldown(0);
    }


    // SECTION: MinecraftClient_BetterCombat

    @Override
    public int getComboCount() {
        return ((PlayerAttackProperties)player).getComboCount();
    }

    @Override
    public boolean hasTargetsInReach() {
        return targetsInReach != null && !targetsInReach.isEmpty();
    }

    @Override
    public float getSwingProgress() {
        if (lastAttacked > lastSwingDuration || lastSwingDuration <= 0) {
            return 1F;
        }
        return (float)lastAttacked / lastSwingDuration;
    }

    @Override
    public int getUpswingTicks() {
        return currentUpswingTicks();
    }

    @Override
    public void cancelUpswing() {
        if (currentUpswingTicks() > 0) {
            cancelWeaponSwing();
        }
    }

    @Override
    public AttackHand getCurrentAttackHand() {
        if (this.ongoingSwing != null) {
            return this.ongoingSwing.attackHand();
        }
        return null;
    }
}
