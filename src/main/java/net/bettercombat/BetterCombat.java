package net.bettercombat;

import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import io.github.kosmx.emotes.server.serializer.type.EmoteSerializerException;
import net.bettercombat.example.ClaymoreItem;
import net.bettercombat.network.WeaponSwingPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterCombat implements ModInitializer {

    public static final String MODID = "bettercombat";

    public static final ClaymoreItem CLAYMORE = new ClaymoreItem(ToolMaterials.IRON, 6, -3.0F, new FabricItemSettings()
            .group(ItemGroup.COMBAT));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("bettercombat", "claymore"), CLAYMORE);
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            loadAnimations("slash");
        });
    }

    public static Map<String, EmoteData> emotes = new HashMap<>();

    private void loadAnimations(String name) {
        try {
            InputStream stream = BetterCombat.class.getResourceAsStream("/assets/" + BetterCombat.MODID + "/animations/" + name + ".json");
            List<EmoteData> emotes = UniversalEmoteSerializer.readData(stream, null, "json");
            EmoteData emote = emotes.get(0);
            BetterCombat.emotes.put(name, emote);
            emote.isBuiltin = true;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
