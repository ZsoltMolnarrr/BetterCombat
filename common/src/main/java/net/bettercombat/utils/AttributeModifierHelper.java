package net.bettercombat.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class AttributeModifierHelper {
    public static @NotNull Multimap<Holder<Attribute>, AttributeModifier> modifierMultimap(ItemStack itemStack) {
        var modifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        Multimap<Holder<Attribute>, AttributeModifier> modifiersMap = HashMultimap.create();
        for (var entry : modifiers.modifiers()) {
            modifiersMap.put(entry.attribute(), entry.modifier());
        }
        return modifiersMap;
    }

    public static @NotNull Multimap<Holder<Attribute>, AttributeModifier> fromModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiersMap = HashMultimap.create();
        modifiersMap.put(attribute, modifier);
        return modifiersMap;
    }
}
