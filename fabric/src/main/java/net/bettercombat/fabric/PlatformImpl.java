package net.bettercombat.fabric;

import net.bettercombat.Platform;
import net.bettercombat.fabric.client.SpellEngineCompatibility;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

import static net.bettercombat.Platform.Type.FABRIC;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return FABRIC;
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean isCastingSpell(PlayerEntity player) {
        return SpellEngineCompatibility.isCastingSpell(player);
    }
}
