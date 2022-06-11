package net.bettercombat;

import me.lortseam.completeconfig.data.Config;
import net.bettercombat.attack.WeaponRegistry;
import net.bettercombat.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class BetterCombat implements ModInitializer {
    public static final String MODID = "bettercombat";
    private static String[] configCategory = {"server"};
    public static ServerConfig config = new ServerConfig();
    public static Config configWrapper = new Config(BetterCombat.MODID, configCategory, config);

    @Override
    public void onInitialize() {
        configWrapper.load();
        ServerNetwork.initializeHandlers();
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            WeaponRegistry.loadAttributes(minecraftServer.getResourceManager());
        });
    }
}