package net.bettercombat;

import net.bettercombat.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class BetterCombat implements ModInitializer {

    public static final String MODID = "bettercombat";

    @Override
    public void onInitialize() {
        ServerNetwork.initializeHandlers();
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            WeaponRegistry.loadAttributes(minecraftServer.getResourceManager());
        });
    }
}
