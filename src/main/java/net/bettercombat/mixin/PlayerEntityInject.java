package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.attack.PlayerAttackHelper;
import net.bettercombat.attack.PlayerAttackProperties;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityInject implements PlayerAttackProperties {

    // FEATURE: Disable sweeping for our weapons

    @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    public Item replaceSword(ItemStack instance) {
        if (WeaponRegistry.getAttributes(instance) != null) {
            return Items.AIR;
        } else {
            return instance.getItem();
        }
    }

    @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    public void playSweep(World instance, PlayerEntity entity, double x, double y, double z, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
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
    }

    // FEATURE: Two-handed weapons

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot == EquipmentSlot.OFFHAND) {
            var mainHandStack = ((PlayerEntityAccessor) this).getInventory().getMainHandStack();
            var attributes = WeaponRegistry.getAttributes(mainHandStack);
            if (attributes != null && attributes.held().isTwoHanded()) {
                cir.setReturnValue(ItemStack.EMPTY);
                cir.cancel();
                return;
            }
        }
        if (slot == EquipmentSlot.MAINHAND) {
            var offHandStack = ((PlayerEntityAccessor)this).getInventory().offHand.get(0);
            var attributes = WeaponRegistry.getAttributes(offHandStack);
            if(attributes != null && attributes.held().isTwoHanded()) {
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

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    public ItemStack getMainHandStack_Redirect(PlayerEntity instance) {
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        return hand.itemStack();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack getStackInHand_Redirect(PlayerEntity instance, Hand handArg) {
        // `handArg` argument is always `MAIN`, we can ignore it
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        return hand.itemStack();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    public void setStackInHand_Redirect(PlayerEntity instance, Hand handArg, ItemStack itemStack) {
        // `handArg` argument is always `MAIN`, we can ignore it
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        var redirectedHand = hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        instance.setStackInHand(redirectedHand, itemStack);
    }
}
