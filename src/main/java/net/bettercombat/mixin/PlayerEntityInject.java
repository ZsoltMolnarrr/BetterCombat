package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityInject {
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

}
