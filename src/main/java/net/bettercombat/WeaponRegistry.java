package net.bettercombat;

import net.bettercombat.api.MeleeWeaponAttributes;
import net.minecraft.util.Identifier;

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
}
