package net.bettercombat.logic;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.utils.PatternMatching;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;

public class WeaponAttributesFallback {
    public static void initialize() {
        var config = BetterCombatMod.fallbackConfig.value;
        for(var itemId: BuiltInRegistries.ITEM.keySet()) {
            var item = BuiltInRegistries.ITEM.getValue(itemId);
            if (PatternMatching.matches(itemId.toString(), config.blacklist_item_id_regex)) {
                // Skipping items without attack damage attribute
                continue;
            }
            FallbackConfig.CompatibilitySpecifier[] specifiers = null;
            if (hasAttributeModifier(item, Attributes.ATTACK_DAMAGE)) {
                specifiers = config.fallback_compatibility;
            } else if (item instanceof ProjectileWeaponItem) {
                specifiers = config.ranged_weapons;
            }
            if (specifiers == null) {
                continue;
            }
            for (var fallbackOption: specifiers) {
                // If - no registration & matches regex
                if (WeaponRegistry.getAttributes(itemId) == null
                        && PatternMatching.matches(itemId.toString(), fallbackOption.item_id_regex)) {
                    var container = WeaponRegistry.containers.get(Identifier.parse(fallbackOption.weapon_attributes));
                    // If assignable attributes are known
                    if (container != null) {
                        WeaponRegistry.resolveAndRegisterAttributes(itemId, container);
                        break; // No more registration attempts for this item id
                    }
                }
            }
        }
    }

    private static boolean hasAttributeModifier(Item item, Holder<Attribute> searchedAttribute) {
        var attributes = item.components().get(DataComponents.ATTRIBUTE_MODIFIERS);
        for (var entry: attributes.modifiers()) {
            var attribute = entry.attribute();
            if (attribute == searchedAttribute || attribute.equals(searchedAttribute)) {
                return true;
            }
        }
        return false;
    }
}
