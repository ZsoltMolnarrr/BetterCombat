package net.bettercombat.client.compat;

import net.minecraft.world.entity.player.Player;

public class SpellEngineCompatibility {
    // TODO: Restore Spell Engine integration (see git history) once a 26.1 build is published
    public static boolean isCastingSpell(Player player) {
        return false;
    }
}
