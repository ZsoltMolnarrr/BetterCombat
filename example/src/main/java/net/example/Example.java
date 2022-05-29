package net.example;

import net.example.items.ClaymoreItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Example implements ModInitializer {

    public static final String MODID = "example";

    public static final ClaymoreItem CLAYMORE = new ClaymoreItem(ToolMaterials.IRON, 1, -3.333f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "claymore"), CLAYMORE);
    }
}
