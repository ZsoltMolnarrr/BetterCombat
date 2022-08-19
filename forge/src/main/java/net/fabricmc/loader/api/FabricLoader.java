package net.fabricmc.loader.api;

import net.minecraftforge.fml.ModList;

public class FabricLoader {
    private static final FabricLoader I = new FabricLoader();
    public static FabricLoader getInstance(){
        return I;
    }

    public boolean isModLoaded(String modid){
        return ModList.get().isLoaded(modid);
    }
}
