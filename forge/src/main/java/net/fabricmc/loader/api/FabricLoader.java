package net.fabricmc.loader.api;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraftforge.fml.ModList;

public interface FabricLoader {
    static FabricLoader getInstance(){
        return FabricLoaderImpl.I;
    }

    boolean isModLoaded(String modid);
}
