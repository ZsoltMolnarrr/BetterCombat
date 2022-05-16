package net.bettercombat.example;

import net.bettercombat.api.AttackStyle;
import net.bettercombat.api.MeleeWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ClaymoreItem extends SwordItem {
    public static MeleeWeaponAttributes attributes = new MeleeWeaponAttributes(
            150,
            2,
            AttackStyle.SLASH_HORIZONTAL_RIGHT_TO_LEFT);

    public static WeaponAttributes attributesV2 = new WeaponAttributes(
            2.5,
            WeaponAttributes.Held.SWORD_TWO_HANDED,
            new WeaponAttributes.Attack[]{
                    new WeaponAttributes.Attack(
                            WeaponAttributes.SwingDirection.HORIZONTAL_RIGHT_TO_LEFT,
                            0.8,
                            150,
                            null,
                            null),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.SwingDirection.FORWARD,
                            1.0,
                            0,
                            null,
                            null),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.SwingDirection.VERTICAL_TOP_TO_BOTTOM,
                            1.2,
                            150,
                            null,
                            null)
            }
    );

    public ClaymoreItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }
}
