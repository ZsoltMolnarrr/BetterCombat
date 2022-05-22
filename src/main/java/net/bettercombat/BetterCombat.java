package net.bettercombat;

import net.bettercombat.example.ClaymoreItem;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BetterCombat implements ModInitializer {

    public static final String MODID = "bettercombat";

    public static final ClaymoreItem CLAYMORE = new ClaymoreItem(ToolMaterials.IRON, 1, -3.333f, new FabricItemSettings()
            .group(ItemGroup.COMBAT));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "claymore"), CLAYMORE);
        WeaponRegistry.register(new Identifier(MODID, "claymore"), ClaymoreItem.attributes);
        WeaponRegistry.updateEncodedRegistry();
        ServerNetwork.initializeHandlers();
    }
}
