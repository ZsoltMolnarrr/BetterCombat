package net.bettercombat.fabric.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.spell_engine.internals.casting.SpellCasterEntity;

public class SpellEngineCompatibility {
    private static Boolean isLoaded = null;
    public static boolean isCastingSpell(PlayerEntity player) {
        if (isLoaded == null) {
            isLoaded = FabricLoader.getInstance().isModLoaded("spell_engine");
        }
        if (isLoaded) {
            // return (SpellCasterEntity) .isCastingSpell(player);
            return ((SpellCasterEntity)player).getCurrentSpell() != null;
        }
        return false;
    }
}
