package net.bettercombat.fabric;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.fabric.attachment.FabricPlayerAttachments;
import net.bettercombat.fabric.network.FabricServerNetwork;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.utils.SoundHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterCombatMod.init();
        
        FabricPlayerAttachments.init();

        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            BetterCombatMod.loadWeaponAttributes(minecraftServer);
        });

        SoundHelper.registerSounds();

        FabricServerNetwork.init();
        BetterCombatParticles.register();
    }
}
