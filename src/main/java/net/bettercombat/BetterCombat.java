package net.bettercombat;

import me.lortseam.completeconfig.data.Config;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.logic.WeaponAttributesFallback;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.ServerNetwork;
import net.bettercombat.utils.SoundHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.tinyconfig.ConfigManager;

public class BetterCombat implements ModInitializer {
    public static final String MODID = "bettercombat";
    private static String[] configCategory = {"server"};
    public static ServerConfig config = new ServerConfig();
    public static Config configWrapper = new Config(BetterCombat.MODID, configCategory, config);
    private static FallbackConfig fallbackDefault = FallbackConfig.createDefault();
    public static ConfigManager<FallbackConfig> fallbackConfig = new ConfigManager<FallbackConfig>
            ("fallback_compatibility", fallbackDefault)
            .builder()
            .setDirectory(MODID)
            .sanitize(true)
            .build();

    @Override
    public void onInitialize() {
        configWrapper.load();
        loadFallbackConfig();
        ServerNetwork.initializeHandlers();
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            WeaponRegistry.loadAttributes(minecraftServer.getResourceManager());
            if (config.fallback_compatibility_enabled) {
                WeaponAttributesFallback.initialize();
            }
            WeaponRegistry.encodeRegistry();
        });
        SoundHelper.registerSounds();
    }

    private void loadFallbackConfig() {
        fallbackConfig.refresh();
        if (fallbackConfig.currentConfig.schema_version < fallbackDefault.schema_version) {
            fallbackConfig.currentConfig = FallbackConfig.migrate(fallbackConfig.currentConfig, FallbackConfig.createDefault());
            fallbackConfig.save();
        }
    }
}