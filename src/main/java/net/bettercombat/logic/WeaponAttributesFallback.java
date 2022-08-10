package net.bettercombat.logic;

import net.bettercombat.BetterCombat;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeaponAttributesFallback {
    public static void initialize() {
        var config = BetterCombat.fallbackConfig.currentConfig;
        for(var itemId: Registry.ITEM.getIds()) {
            var item = Registry.ITEM.get(itemId);
            if (!hasAttributeModifier(item, EntityAttributes.GENERIC_ATTACK_DAMAGE)
                    || matches(itemId.toString(), config.blacklist_item_id_regex)) {
                // Skipping items without attack damage attribute
                continue;
            }
            for (var fallbackOption: config.fallback_compatibility) {
                // If - no registration & matches regex
                if (WeaponRegistry.getAttributes(itemId) == null
                        && matches(itemId.toString(), fallbackOption.item_id_regex)) {
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

    private static boolean matches(String subject, String nullableRegex) {
        if (subject == null) {
            return false;
        }
        if (nullableRegex == null || nullableRegex.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(nullableRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }
}
