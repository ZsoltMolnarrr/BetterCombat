package net.bettercombat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.BetterCombat;
import net.bettercombat.PlatformClient;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.BetterCombatKeybindings;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.logic.*;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.PatternMatching;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow @Final private static Logger LOGGER;

    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }
    private boolean isHoldingAttackInput = false;
    private boolean isHarvesting = false;
    private String textToRender = null;
    private int textFade = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(RunArgs args, CallbackInfo ci) {
        setupTextRenderer();
    }

    // Targeting the method where all the disconnection related logic is.
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",at = @At("TAIL"))
    private void disconnect_TAIL(Screen screen, CallbackInfo ci) {
        BetterCombatClient.serverCombatMode = CombatMode.ANIMATIONS_ONLY;
        WeaponRegistry.clear();
    }

    private void setupTextRenderer() {
        HudRenderCallback.EVENT.register((context, f) -> {
            if (textToRender != null && !textToRender.isEmpty()) {
                var client = MinecraftClient.getInstance();
                var textRenderer = client.inGameHud.getTextRenderer();
                var scaledWidth = client.getWindow().getScaledWidth();
                var scaledHeight = client.getWindow().getScaledHeight();

                int i = textRenderer.getWidth(textToRender);
                int j = (scaledWidth - i) / 2;
                int k = scaledHeight - 59 - 14;
                int l = 0;
                if (!client.interactionManager.hasStatusBars()) {
                    k += 14;
                }
                if ((l = (int)((float)this.textFade * 256.0f / 10.0f)) > 255) {
                    l = 255;
                }
                if (l > 0) {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    context.fill(j - 2, k - 2, j + i + 2, k + textRenderer.fontHeight + 2, client.options.getTextBackgroundColor(0));
                    context.drawTextWithShadow(textRenderer, textToRender, j, k, 0xFFFFFF + (l << 24));
                    RenderSystem.disableBlend();
                }
            }
            if (textFade <= 0) {
                textToRender = null;
            }
        });
    }

    // Press to attack
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;

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
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;

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

            if (BetterCombatClient.config.isHoldToAttackEnabled && isPressed) {
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
        LOGGER.info("Nope");
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;

        var hand = getCurrentHand();
        if (hand == null) { return; }
        double upswingRate = hand.attack().upswingRate();
        if (upswingTicks > 0 || player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            ci.cancel();
        }
    }

    private boolean isTargetingMineableBlock() {
        if (!BetterCombatClient.config.isMiningWithWeaponsEnabled) {
            return false;
        }
        var regex = BetterCombatClient.config.mineWithWeaponBlacklist;
        if (regex != null && !regex.isEmpty()) {
            var itemStack = player.getMainHandStack();
            var id = Registries.ITEM.getId(itemStack.getItem()).toString();
            if (PatternMatching.matches(id, regex)) {
                return false;
            }
        }
        if (BetterCombatClient.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled
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
        if(!BetterCombatClient.config.isSwingThruGrassEnabled) {
            return false;
        }
        var regex = BetterCombatClient.config.swingThruGrassBlacklist;
        if (regex == null || regex.isEmpty()) {
            return true;
        }
        var itemStack = player.getMainHandStack();
        var id = Registries.ITEM.getId(itemStack.getItem()).toString();
        return !PatternMatching.matches(id, regex);
    }

    private ItemStack upswingStack;
    private ItemStack lastAttacedWithItemStack;
    private int upswingTicks = 0;
    private int lastAttacked = 1000;
    private float lastSwingDuration = 0;
    private int comboReset = 0;

    private void startUpswing(WeaponAttributes attributes) {
        // Guard conditions

        if (player.isRiding()) {
            // isRiding is `isHandsBusy()` according to official mappings
            // Support for revival mod
            return;
        }

        var hand = getCurrentHand();
        if (hand == null) { return; }
        float upswingRate = (float) hand.attack().upswingRate();
        if (upswingTicks > 0
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
        this.comboReset = Math.round(attackCooldownTicksFloat * BetterCombat.config.combo_reset_rate);
        this.upswingTicks = Math.max(Math.round(attackCooldownTicksFloat * upswingRate), 1); // At least 1 upswing ticks
        this.lastSwingDuration = attackCooldownTicksFloat;
        this.itemUseCooldown = attackCooldownTicks; // Vanilla MinecraftClient property for compatibility
        setMiningCooldown(attackCooldownTicks);
//        System.out.println("Starting upswingTicks: " + upswingTicks);
        String animationName = hand.attack().animation();
        boolean isOffHand = hand.isOffHand();
        var animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());
        ((PlayerAttackAnimatable) player).playAttackAnimation(animationName, animatedHand, attackCooldownTicksFloat, upswingRate);
        ClientPlayNetworking.send(
                Packets.AttackAnimation.ID,
                new Packets.AttackAnimation(player.getId(), animatedHand, animationName, attackCooldownTicksFloat, upswingRate).write());
        BetterCombatClientEvents.ATTACK_START.invoke(handler -> {
            handler.onPlayerAttackStart(player, hand);
        });
    }

    private void cancelSwingIfNeeded() {
        if (upswingStack != null && !areItemStackEqual(player.getMainHandStack(), upswingStack)) {
            cancelWeaponSwing();
            return;
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
        if(BetterCombatClient.config.isHighlightCrosshairEnabled
                || BetterCombatClient.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled) {
            return targetsInReach == null;
        }
        return false;
    }

    private void updateTargetsInReach(List<Entity> targets) {
        targetsInReach = targets;
    }

    private void updateTargetsIfNeeded() {
        if (shouldUpdateTargetsInReach()) {
            var hand = PlayerAttackHelper.getCurrentAttack(player, getComboCount());
            WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
            List<Entity> targets = List.of();
            if (attributes != null && attributes.attacks() != null) {
                targets = TargetFinder.findAttackTargets(
                        player,
                        getCursorTarget(),
                        hand.attack(),
                        attributes.attackRange());
            }
            updateTargetsInReach(targets);
        }
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void pre_Tick(CallbackInfo ci) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;
        if (player == null) {
            return;
        }
        targetsInReach = null;
        lastAttacked += 1;
        cancelSwingIfNeeded();
        attackFromUpswingIfNeeded();
        updateTargetsIfNeeded();
        resetComboIfNeeded();
    }

    @Inject(method = "tick",at = @At("TAIL"))
    private void post_Tick(CallbackInfo ci) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;
        if (player == null) {
            return;
        }
        if (BetterCombatKeybindings.toggleMineKeyBinding.wasPressed()) {
            BetterCombatClient.config.isMiningWithWeaponsEnabled = !BetterCombatClient.config.isMiningWithWeaponsEnabled;
            AutoConfig.getConfigHolder(ClientConfigWrapper.class).save();
            textToRender = I18n.translate(BetterCombatClient.config.isMiningWithWeaponsEnabled ?
                    "hud.bettercombat.mine_with_weapons_on" : "hud.bettercombat.mine_with_weapons_off");
            textFade = 40;
        }
        if (textFade > 0) {
            textFade -= 1;
        }
    }

    private void performAttack() {
        if (BetterCombatKeybindings.feintKeyBinding.isPressed()) {
            player.resetLastAttackedTicks();
            cancelWeaponSwing();
            return;
        }

        var hand = getCurrentHand();
        if (hand == null) { return; }
        var attack = hand.attack();
        var upswingRate = hand.attack().upswingRate();
        if (player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
            return;
        }
        // System.out.println("Attack with CD: " + client.player.getAttackCooldownProgress(0));

        var cursorTarget = getCursorTarget();
        List<Entity> targets = TargetFinder.findAttackTargets(
                player,
                cursorTarget,
                attack,
                hand.attributes().attackRange());
        updateTargetsInReach(targets);
        if(targets.size() == 0) {
            PlatformClient.onEmptyLeftClick(player);
        }

        // Mimic logic of:
        // ClientPlayerInteractionManager.attackEntity(PlayerEntity player, Entity target)
        ClientPlayNetworking.send(
                Packets.C2S_AttackRequest.ID,
                new Packets.C2S_AttackRequest(getComboCount(), player.isSneaking(), player.getInventory().selectedSlot, targets).write());
        for (var target: targets) {
            player.attack(target);
        }
        player.resetLastAttackedTicks();
        BetterCombatClientEvents.ATTACK_HIT.invoke(handler -> {
            handler.onPlayerAttackStart(player, hand, targets, cursorTarget);
        });

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
        var downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (1 - 0.5 * BetterCombat.config.upswing_multiplier));
        ((PlayerAttackAnimatable) player).stopAttackAnimation(downWind);
        ClientPlayNetworking.send(
                Packets.AttackAnimation.ID,
                Packets.AttackAnimation.stop(player.getId(), downWind).write());
        upswingStack = null;
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
        return upswingTicks;
    }

    @Override
    public void cancelUpswing() {
        if (upswingTicks > 0) {
            cancelWeaponSwing();
        }
    }
}
