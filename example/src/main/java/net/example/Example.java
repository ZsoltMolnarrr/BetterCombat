package net.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Example implements ModInitializer {

    public static final String MODID = "example";

    public static final SwordItem CLAYMORE = new SwordItem(ToolMaterials.IRON, 1, -3.333f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem BONECLUB = new SwordItem(ToolMaterials.IRON, 1, -3.5f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem DAGGER = new SwordItem(ToolMaterials.IRON, 1, -2f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem DOUBLEAXE = new SwordItem(ToolMaterials.IRON, 1, -3f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem FIST = new SwordItem(ToolMaterials.IRON, 1, -3f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem GLAIVE = new SwordItem(ToolMaterials.IRON, 1, -3f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem MACE = new SwordItem(ToolMaterials.IRON, 1, -3f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem SPEAR = new SwordItem(ToolMaterials.IRON, 1, -3f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));
    public static final SwordItem STAFF = new SwordItem(ToolMaterials.IRON, 1, -3f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "claymore"), CLAYMORE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "bone_club"), BONECLUB);
        Registry.register(Registry.ITEM, new Identifier(MODID, "dagger"), DAGGER);
        Registry.register(Registry.ITEM, new Identifier(MODID, "double_axe"), DOUBLEAXE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "fist"), FIST);
        Registry.register(Registry.ITEM, new Identifier(MODID, "glaive"), GLAIVE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "mace"), MACE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "spear"), SPEAR);
        Registry.register(Registry.ITEM, new Identifier(MODID, "staff"), STAFF);
    }
}
