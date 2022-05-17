package net.bettercombat;

import net.bettercombat.api.MeleeWeaponAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class WeaponRegistry {
    private static Map<Identifier, MeleeWeaponAttributes> registrations = new HashMap();

    public static void register(Identifier itemId, MeleeWeaponAttributes attributes) {
        registrations.put(itemId, attributes);
    }

    public static MeleeWeaponAttributes getAttributes(Identifier itemId) {
        return registrations.get(itemId);
    }

    public static MeleeWeaponAttributes getAttributes(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        Item item = itemStack.getItem();
        Identifier id = Registry.ITEM.getId(item);
        MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);
        return attributes;
    }
}
