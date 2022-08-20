package ca.lukegrahamlandry.bettercombat;

import net.bettercombat.BetterCombat;
import net.bettercombat.client.BetterCombatClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

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
        List<String> soundKeys = List.of(
                "anchor_slam",
                "axe_slash",
                "claymore_swing",
                "claymore_stab",
                "claymore_slam",
                "dagger_slash",
                "double_axe_swing",
                "fist_punch",
                "glaive_slash_quick",
                "glaive_slash_slow",
                "hammer_slam",
                "katana_slash",
                "mace_slam",
                "mace_slash",
                "pickaxe_swing",
                "rapier_slash",
                "rapier_stab",
                "scythe_slash",
                "spear_stab",
                "staff_slam",
                "staff_slash",
                "staff_spin",
                "staff_stab",
                "sickle_slash",
                "sword_slash",
                "wand_swing"
        );
        for (var soundKey: soundKeys) {
            BetterCombatForge.SOUNDS.register(soundKey, () -> new SoundEvent(new Identifier(BetterCombat.MODID, soundKey)));
        }
    }
}
