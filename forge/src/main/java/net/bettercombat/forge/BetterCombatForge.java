package net.bettercombat.forge;

import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.forge.network.NetworkHandler;
import net.bettercombat.BetterCombat;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BetterCombat.MODID)
public class BetterCombatForge {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterCombat.MODID);

    public BetterCombatForge() {
        new BetterCombat().onInitialize();
        NetworkHandler.registerMessages();

        registerSounds();
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
            return new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> {
                return AutoConfig.getConfigScreen(ClientConfigWrapper.class, screen).get();
            });
        });
    }

    private void registerSounds() {
        for (var soundKey: SoundHelper.soundKeys) {
            BetterCombatForge.SOUNDS.register(soundKey, () -> new SoundEvent(new Identifier(BetterCombat.MODID, soundKey)));
        }
    }
}
