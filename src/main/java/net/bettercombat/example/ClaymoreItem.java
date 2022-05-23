package net.bettercombat.example;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ClaymoreItem extends SwordItem {
    public static WeaponAttributes attributes = new WeaponAttributes(
            2.5,
            WeaponAttributes.Held.SWORD_TWO_HANDED,
            new WeaponAttributes.Attack[]{
                    new WeaponAttributes.Attack(
                            WeaponAttributes.SwingDirection.HORIZONTAL_RIGHT_TO_LEFT,
                            -0.5,
                            150,
                            0.3,
                            "slash",
                            new WeaponAttributes.Sound[] {
                                    new WeaponAttributes.Sound(
                                            BetterCombat.MODID + ":" + "claymore-swing",
                                            1F,
                                            1F,
                                            0.1F)
                            },
                            null),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.SwingDirection.FORWARD,
                            0,
                            0,
                            0.3,
                            "slash",
                            new WeaponAttributes.Sound[] {
                                    new WeaponAttributes.Sound(
                                            BetterCombat.MODID + ":" + "claymore-stab",
                                            1F,
                                            1F,
                                            0.1F)
                            },
                            null),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.SwingDirection.VERTICAL_TOP_TO_BOTTOM,
                            0.5,
                            150,
                            0.3,
                            "slash",
                            new WeaponAttributes.Sound[] {
                                    new WeaponAttributes.Sound(
                                            BetterCombat.MODID + ":" + "claymore-slam",
                                            1F,
                                            1F,
                                            0.1F)
                            },
                            null)
            }
    );

    public ClaymoreItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }
}
