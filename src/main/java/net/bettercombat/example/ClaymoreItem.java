package net.bettercombat.example;

import bettercombat.bettercombat.api.MeleeWeapon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ClaymoreItem extends SwordItem implements MeleeWeapon {
    public ClaymoreItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
//        playerEntity.playSound(ExampleMod.SOUND_CLAYMORE_SWING, 1.0F, 1.0F);
//        return TypedActionResult.success(playerEntity.getStackInHand(hand));
//    }
//
//    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//        target.playSound(ExampleMod.SOUND_CLAYMORE_IMPACT, 5.0F, 1.0F);
//        return super.postHit(stack, target, attacker);
//    }
}
