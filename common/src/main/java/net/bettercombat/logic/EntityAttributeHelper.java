package net.bettercombat.logic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class EntityAttributeHelper {
    public static boolean itemHasRangeAttribute(ItemStack stack) {
        var attributeModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (attributeModifiers != null) {
            for(var modifier: attributeModifiers.modifiers()) {
                if (modifier.attribute().value().equals(Attributes.ENTITY_INTERACTION_RANGE.value())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int rangeModifierCount(ItemStack stack) {
        var attributeModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (attributeModifiers != null) {
            int count = 0;
            for(var modifier: attributeModifiers.modifiers()) {
                if (modifier.attribute().value().equals(Attributes.ENTITY_INTERACTION_RANGE.value())) {
                    count += 1;
                }
            }
            return count;
        }
        return 0;
    }
}
