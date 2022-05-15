package net.bettercombat.example;

import net.bettercombat.api.AttackStyle;
import net.bettercombat.api.MeleeWeaponAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ClaymoreItem extends SwordItem {
    public static MeleeWeaponAttributes attributes = new MeleeWeaponAttributes(
            150,
            2,
            AttackStyle.SLASH_HORIZONTAL_RIGHT_TO_LEFT);

    public ClaymoreItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }
}
