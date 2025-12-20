package net.bettercombat.mixin.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.logic.PlayerAttachments;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 899)
public abstract class PlayerEntityMixin implements PlayerAttackProperties, EntityPlayer_BetterCombat {
    private int comboCount = 0;
    public int getComboCount() {
        return comboCount;
    }
    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void post_Tick(CallbackInfo ci) {
        var instance = (Object)this;
        var player = ((PlayerEntity)instance);

        if (player.getEntityWorld().isClient()) {
            ((PlayerAttackAnimatable) this).updateAnimationsOnTick();
        } else {
            var pose = PlayerAttackHelper.poseForPlayer(player);
            Platform.playerAttachments().setMainHandIdleAnimation(player, pose.base());
            Platform.playerAttachments().setOffHandIdleAnimation(player, pose.offHand());
        }
        updateDualWieldingSpeedBoost();
    }

    public String getMainHandIdleAnimation() {
        return Platform.playerAttachments().getMainHandIdleAnimation(((PlayerEntity) ((Object)this)));
    }

    public String getOffHandIdleAnimation() {
        return Platform.playerAttachments().getOffHandIdleAnimation(((PlayerEntity) ((Object)this)));
    }

    // FEATURE: Disable sweeping for attributed weapons

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    private boolean disableSweeping(boolean value) {
        if (BetterCombatMod.config.allow_vanilla_sweeping) {
            return value;
        }

        var player = ((PlayerEntity) ((Object)this));
        var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (currentHand != null) {
            // Disable sweeping
            return false;
        }
        return value;
    }

    // FEATURE: Two-handed wielding - Moved into `LivingEntityMixin`

    // FEATURE: Dual wielding

    private Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> dualWieldingAttributeMap;
    private static final Identifier dualWieldingSpeedModifierId = Identifier.of(BetterCombatMod.ID, "dual_wield");


    // FIXME: Replace with high level multiplied Mixin, WrapOperation player.getAttributes(...)
    private void updateDualWieldingSpeedBoost() {
        var player = ((PlayerEntity) ((Object)this));
        var newState = PlayerAttackHelper.isDualWielding(player);
        var currentState = dualWieldingAttributeMap != null;
        if (newState != currentState) {
            if(newState) {
                // Just started dual wielding
                // Adding speed boost modifier
                this.dualWieldingAttributeMap = HashMultimap.create();
                double multiplier = BetterCombatMod.config.dual_wielding_attack_speed_multiplier - 1;
                dualWieldingAttributeMap.put(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                dualWieldingSpeedModifierId,
                                multiplier,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributes().addTemporaryModifiers(dualWieldingAttributeMap);
            } else {
                // Just stopped dual wielding
                // Removing speed boost modifier
                if (dualWieldingAttributeMap != null) { // Safety first... Who knows...
                    player.getAttributes().removeModifiers(dualWieldingAttributeMap);
                    dualWieldingAttributeMap = null;
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canUseSweepAttack", cancellable = true)
    public void canUseSweepAttack_HEAD(boolean cooldownPassed, boolean criticalHit, boolean knockback, CallbackInfoReturnable<Boolean> cir) {
        if (!BetterCombatMod.config.allow_vanilla_sweeping) {
            var player = ((PlayerEntity) ((Object)this));
            var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
            if (currentHand != null) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

//    private AttackHand lastAttack;

//    @Redirect(method = "attack", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
//    public ItemStack getMainHandStack_Redirect(PlayerEntity instance) {
//        // DUAL WIELDING LOGIC
//        // Here we return the off-hand stack as fake main-hand, purpose:
//        // - Getting enchants
//        // - Getting itemstack to be damaged
//        if (comboCount < 0) {
//            // Vanilla behaviour
//            return instance.getMainHandStack();
//        }
//        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
//        if (hand == null) {
//            var isOffHand = PlayerAttackHelper.shouldAttackWithOffHand(instance, comboCount);
//            if (isOffHand) {
//                return ItemStack.EMPTY;
//            } else {
//                return instance.getMainHandStack();
//            }
//        }
//        lastAttack = hand;
//        return hand.itemStack();
//    }
//
//    @Redirect(method = "attack", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
//    public void setStackInHand_Redirect(PlayerEntity instance, Hand handArg, ItemStack itemStack) {
//        // DUAL WIELDING LOGIC
//        // In case item got destroyed due to durability loss
//        // We empty the correct hand
//        if (comboCount < 0) {
//            // Vanilla behaviour
//            instance.setStackInHand(handArg, itemStack);
//        }
//        // `handArg` argument is always `MAIN`, we can ignore it
//        AttackHand hand = lastAttack;
//        if (hand == null) {
//            hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
//        }
//        if (hand == null) {
//            instance.setStackInHand(handArg, itemStack);
//            return;
//        }
//        var redirectedHand = hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
//        instance.setStackInHand(redirectedHand, itemStack);
//    }


    // FIXME: We may need this

//    @Inject(at = @At("HEAD"), method = "getWeaponStack", cancellable = true)
//    private void getWeaponStack_HEAD(CallbackInfoReturnable<ItemStack> cir) {
//        // DUAL WIELDING LOGIC
//        // Here we return the off-hand stack as fake main-hand, purpose:
//        // - Getting enchants
//        // - Getting itemstack to be damaged
//
//        var player = ((PlayerEntity) ((Object)this));
//        var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
//        if (currentHand != null) {
//            cir.setReturnValue(currentHand.itemStack());
//            cir.cancel();
//        }
//    }


    // SECTION: BetterCombatPlayer

    @Nullable
    public AttackHand getCurrentAttack() {
        if (comboCount < 0) {
            return null;
        }
        var player = ((PlayerEntity) ((Object)this));
        return PlayerAttackHelper.getCurrentAttack(player, comboCount);
    }
}
