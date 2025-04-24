package net.bettercombat.logic;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.utils.PatternMatching;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class WeaponAttributesFallback {
    public static void initialize() {
        var config = BetterCombatMod.fallbackConfig.value;
        for(var itemId: Registries.ITEM.getIds()) {
            var item = Registries.ITEM.get(itemId);
            if (PatternMatching.matches(itemId.toString(), config.blacklist_item_id_regex)) {
                // Skipping items without attack damage attribute
                continue;
            }
            FallbackConfig.CompatibilitySpecifier[] specifiers = null;
            if (hasAttributeModifier(item, EntityAttributes.ATTACK_DAMAGE)) {
                specifiers = config.fallback_compatibility;
            } else if (item instanceof RangedWeaponItem) {
                specifiers = config.ranged_weapons;
            }
            if (specifiers == null) {
                continue;
            }
            for (var fallbackOption: specifiers) {
                // If - no registration & matches regex
                if (WeaponRegistry.getAttributes(itemId) == null
                        && PatternMatching.matches(itemId.toString(), fallbackOption.item_id_regex)) {
                    var container = WeaponRegistry.containers.get(Identifier.of(fallbackOption.weapon_attributes));
                    // If assignable attributes are known
                    if (container != null) {
                        WeaponRegistry.resolveAndRegisterAttributes(itemId, container);
                        break; // No more registration attempts for this item id
                    }
                }
            }
        }
    }

    private static boolean hasAttributeModifier(Item item, RegistryEntry<EntityAttribute> searchedAttribute) {
        var attributes = item.getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        for (var entry: attributes.modifiers()) {
            var attribute = entry.attribute();
            if (attribute == searchedAttribute || attribute.equals(searchedAttribute)) {
                return true;
            }
        }
        return false;
    }
}
