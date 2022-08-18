package net.bettercombat.helper;

import net.bettercombat.BetterCombat;
import net.bettercombat.helper.network.forge.NetworkHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BetterCombat.MODID)
public class BetterCombatForge {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterCombat.MODID);

    public BetterCombatForge(){
        new BetterCombat().onInitialize();
        NetworkHandler.registerMessages();
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
