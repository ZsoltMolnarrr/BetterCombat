package net.bettercombat.forge;

import net.bettercombat.BetterCombat;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BetterCombat.MODID)
public class BetterCombatForge {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterCombat.MODID);

    public BetterCombatForge(){
        new BetterCombat().onInitialize();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new BetterCombatClient().onInitializeClient());

        registerSounds();
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void registerSounds(){
        for (var soundKey: SoundHelper.soundKeys) {
            BetterCombatForge.SOUNDS.register(soundKey, () -> new SoundEvent(new Identifier(BetterCombat.MODID, soundKey)));
        }
    }
}
