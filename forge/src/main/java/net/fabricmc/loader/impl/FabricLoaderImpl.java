package net.fabricmc.loader.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.ModList;

public class FabricLoaderImpl implements FabricLoader{
    public static final FabricLoader I = new FabricLoaderImpl();

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
