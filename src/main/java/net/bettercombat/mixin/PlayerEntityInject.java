package net.bettercombat.mixin;

import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.entity.EquipmentSlot.OFFHAND;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityInject implements PlayerAttackProperties {
    @Inject(method = "tick", at = @At("TAIL"))
    public void post_Tick(CallbackInfo ci) {
        var instance = (Object)this;
        if (((PlayerEntity)instance).world.isClient()) {
            ((PlayerAttackAnimatable) this).updateAnimationsOnTick();
        }
    }

    // FEATURE: Disable sweeping for our weapons

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    private boolean disableSweeping(boolean value) {
        var player = ((PlayerEntity) ((Object)this) );
        var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (currentHand != null) {
            return false;
        }
        return value;
    }

//    @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
//    public void playSweep(World instance, PlayerEntity entity, double x, double y, double z, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
//        // Get an instance of player entity because player is always null here
//        PlayerEntity playerEntity = (PlayerEntity) ((Object)this);
//        // Create a list with sword sweep sounds
//        List<Identifier> swordSweepSounds = new ArrayList<>();
//        swordSweepSounds.add(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP.getId());
//        swordSweepSounds.add(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK.getId());
//        swordSweepSounds.add(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG.getId());
//        swordSweepSounds.add(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT.getId());
//        // If the player has on the main hand a sword and the event for the sound is any of the attack sounds
//        if (playerEntity.getMainHandStack().getItem() instanceof SwordItem && swordSweepSounds.contains(soundEvent.getId())) {
//            // Play an anvil landing sound
//            instance.playSound(null, x, y, z, SoundEvents.BLOCK_ANVIL_LAND, soundCategory, volume, pitch);
//        } else {
//            // Otherwise play the sound that the game chose by default
//            instance.playSound(null, x, y, z, soundEvent, soundCategory, volume, pitch);
//        }
//    }

    // FEATURE: Two-handed wielding

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        var mainHandHasTwoHanded = false;
        var mainHandStack = ((PlayerEntityAccessor) this).getInventory().getMainHandStack();
        var mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
        if (mainHandAttributes != null && mainHandAttributes.isTwoHanded()) {
            mainHandHasTwoHanded = true;
        }

        var offHandHasTwoHanded = false;
        var offHandStack = ((PlayerEntityAccessor)this).getInventory().offHand.get(0);
        var offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
        if(offHandAttributes != null && offHandAttributes.isTwoHanded()) {
            offHandHasTwoHanded = true;
        }

        if (slot == OFFHAND) {
            if (mainHandHasTwoHanded || offHandHasTwoHanded) {
                cir.setReturnValue(ItemStack.EMPTY);
                cir.cancel();
                return;
            }
        }
    }

    // FEATURE: Dual wielded attacking

    int comboCount = 0;

    public int getComboCount() {
        return comboCount;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    @Redirect(method = "getAttackCooldownProgress", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgressPerTick()F"))
    public float getAttackCooldownProgressPerTick_Redirect(PlayerEntity instance) {
        // `getAttackCooldownProgressPerTick` should be called `getAttackCooldownTicks`
        return PlayerAttackHelper.getScaledAttackCooldown(instance);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    public ItemStack getMainHandStack_Redirect(PlayerEntity instance) {
        if (comboCount < 0) {
            // Vanilla behaviour
            return instance.getMainHandStack();
        }
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        if (hand == null) {
            var isOffHand = PlayerAttackHelper.shouldAttackWithOffHand(instance, comboCount);
            if (isOffHand) {
                return ItemStack.EMPTY;
            } else {
                return instance.getMainHandStack();
            }
        }
        return hand.itemStack();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack getStackInHand_Redirect(PlayerEntity instance, Hand handArg) {
        if (comboCount < 0) {
            // Vanilla behaviour
            return instance.getStackInHand(handArg);
        }
        // `handArg` argument is always `MAIN`, we can ignore it
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        if (hand == null) {
            var isOffHand = PlayerAttackHelper.shouldAttackWithOffHand(instance, comboCount);
            if (isOffHand) {
                return ItemStack.EMPTY;
            } else {
                return instance.getStackInHand(handArg);
            }
        }
        return hand.itemStack();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    public void setStackInHand_Redirect(PlayerEntity instance, Hand handArg, ItemStack itemStack) {
        if (comboCount < 0) {
            // Vanilla behaviour
            instance.setStackInHand(handArg, itemStack);
        }
        // `handArg` argument is always `MAIN`, we can ignore it
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        if (hand == null) {
            instance.setStackInHand(handArg, itemStack);
            return;
        }
        var redirectedHand = hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        instance.setStackInHand(redirectedHand, itemStack);
    }
}
