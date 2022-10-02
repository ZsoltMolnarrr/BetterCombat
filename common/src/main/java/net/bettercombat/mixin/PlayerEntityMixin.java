package net.bettercombat.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static net.minecraft.entity.EquipmentSlot.OFFHAND;

@Mixin(PlayerEntity.class)
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
        if (((PlayerEntity)instance).world.isClient()) {
            ((PlayerAttackAnimatable) this).updateAnimationsOnTick();
        }
        updateDualWieldingSpeedBoost();
    }

    // FEATURE: Disable sweeping for attributed weapons

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    private boolean disableSweeping(boolean value) {
        if (BetterCombat.config.allow_sweeping) {
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

    // FEATURE: Dual wielding

    private Multimap<EntityAttribute, EntityAttributeModifier> dualWieldingAttributeMap;
    private static UUID dualWieldingSpeedModifierId = UUID.fromString("6b364332-0dc4-11ed-861d-0242ac120002");

    private void updateDualWieldingSpeedBoost() {
        var player = ((PlayerEntity) ((Object)this));
        var newState = PlayerAttackHelper.isDualWielding(player);
        var currentState = dualWieldingAttributeMap != null;
        if (newState != currentState) {
            if(newState) {
                // Just started dual wielding
                // Adding speed boost modifier
                this.dualWieldingAttributeMap = HashMultimap.create();
                double multiplier = BetterCombat.config.dual_wielding_attack_speed_multiplier - 1;
                dualWieldingAttributeMap.put(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(
                                dualWieldingSpeedModifierId,
                                "Dual wielding attack speed boost",
                                multiplier,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE));
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

    @ModifyArg(method = "attack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"),
            index = 0)
    public Hand getHand(Hand hand) {
        var player = ((PlayerEntity) ((Object)this) );
        var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (currentHand != null) {
            return currentHand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        } else {
            return Hand.MAIN_HAND;
        }
    }

    private AttackHand lastAttack;

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    public ItemStack getMainHandStack_Redirect(PlayerEntity instance) {
        // DUAL WIELDING LOGIC
        // Here we return the off-hand stack as fake main-hand, purpose:
        // - Getting enchants
        // - Getting itemstack to be damaged
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
        lastAttack = hand;
        return hand.itemStack();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    public void setStackInHand_Redirect(PlayerEntity instance, Hand handArg, ItemStack itemStack) {
        // DUAL WIELDING LOGIC
        // In case item got destroyed due to durability loss
        // We empty the correct hand
        if (comboCount < 0) {
            // Vanilla behaviour
            instance.setStackInHand(handArg, itemStack);
        }
        // `handArg` argument is always `MAIN`, we can ignore it
        AttackHand hand = lastAttack;
        if (hand == null) {
            hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        }
        if (hand == null) {
            instance.setStackInHand(handArg, itemStack);
            return;
        }
        var redirectedHand = hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        instance.setStackInHand(redirectedHand, itemStack);
    }

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
