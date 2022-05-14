package net.bettercombat.example;

import net.bettercombat.api.AttackStyle;
import net.bettercombat.api.MeleeWeaponAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ClaymoreItem extends SwordItem {

    public static MeleeWeaponAttributes attributes = new MeleeWeaponAttributes(180, 2, AttackStyle.SLASH_HORIZONTAL_RIGHT_TO_LEFT);

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
