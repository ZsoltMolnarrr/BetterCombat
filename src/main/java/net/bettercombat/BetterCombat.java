package net.bettercombat;

import net.bettercombat.example.ClaymoreItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BetterCombat implements ModInitializer {

    public static final ClaymoreItem CLAYMORE = new ClaymoreItem(ToolMaterials.IRON, 6, -3.0F, new FabricItemSettings()
            .group(ItemGroup.COMBAT));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("bettercombat", "claymore"), CLAYMORE);
    }
}
