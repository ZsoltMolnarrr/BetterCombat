package net.bettercombat.client.compat;

import net.bettercombat.Platform;
import net.minecraft.world.entity.player.Player;
import net.spell_engine.internals.casting.SpellCasterEntity;

public class SpellEngineCompatibility {
    private static Boolean isLoaded = null;
    public static boolean isCastingSpell(Player player) {
        if (isLoaded == null) {
            isLoaded = Platform.isModLoaded("spell_engine");
        }
        if (isLoaded) {
            // return (SpellCasterEntity) .isCastingSpell(player);
            return ((SpellCasterEntity)player).getCurrentSpell() != null;
        }
        return false;
    }
}
