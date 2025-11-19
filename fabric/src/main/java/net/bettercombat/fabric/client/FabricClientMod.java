package net.bettercombat.fabric.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.Keybindings;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.bettercombat.client.particle.SlashParticle;
import net.bettercombat.particle.BetterCombatParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BetterCombatClientMod.init();
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            BetterCombatClientMod.loadAnimation();
        });
        for (var keybinding : Keybindings.all) {
            KeyBindingHelper.registerKeyBinding(keybinding);
        }
        ItemTooltipCallback.EVENT.register((itemStack, context, type, lines) -> {
            WeaponAttributeTooltip.modifyTooltip(itemStack, lines);
        });
        ModelPredicateProviderRegistry.register(Identifier.of(BetterCombatMod.ID, "loaded"), (stack, world, entity, seed) -> {
            return 1.0F;
        });
        FabricClientNetwork.init();
        for (var entry: BetterCombatParticles.ENTRIES) {
            ParticleFactoryRegistry.getInstance().register(
                    entry.particleType(), (provider) -> new SlashParticle.Provider(provider, entry.params())
            );
        }
    }
}
