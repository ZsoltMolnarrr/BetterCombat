package net.bettercombat.logic;

import net.bettercombat.BetterCombat;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WeaponAttributesFallback {
    public static void initialize() {
        var config = BetterCombat.fallbackConfig.value;
        for(var itemId: Registry.ITEM.getIds()) {
            var item = Registry.ITEM.get(itemId);
            if (!hasAttributeModifier(item, EntityAttributes.GENERIC_ATTACK_DAMAGE)
                    || PatternMatching.matches(itemId.toString(), config.blacklist_item_id_regex)) {
                // Skipping items without attack damage attribute
                continue;
            }
            for (var fallbackOption: config.fallback_compatibility) {
                // If - no registration & matches regex
                if (WeaponRegistry.getAttributes(itemId) == null
                        && PatternMatching.matches(itemId.toString(), fallbackOption.item_id_regex)) {
                    var container = WeaponRegistry.containers.get(new Identifier(fallbackOption.weapon_attributes));
                    // If assignable attributes are known
                    if (container != null) {
                        WeaponRegistry.resolveAndRegisterAttributes(itemId, container);
                        break; // No more registration attempts for this item id
                    }
                }
            }
        }
    }

    private static boolean hasAttributeModifier(Item item, EntityAttribute searchedAttribute) {
        var searchedAttributeId = Registry.ATTRIBUTE.getId(searchedAttribute);
        var attributes = item.getAttributeModifiers(EquipmentSlot.MAINHAND);
        for (var entry: attributes.entries()) {
            var attribute = entry.getKey();
            var attributeId = Registry.ATTRIBUTE.getId(attribute);
            if (attributeId.equals(searchedAttributeId)) {
                return true;
            }
        }
        return false;
    }
}
