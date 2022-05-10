package net.bettercombat.client;

import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import net.bettercombat.BetterCombat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BetterCombatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            loadAnimations("slash");
        });
    }

    public static Map<String, EmoteData> emotes = new HashMap<>();

    private void loadAnimations(String name) {
        try {
            InputStream stream = BetterCombatClient.class.getResourceAsStream("/assets/" + BetterCombat.MODID + "/animations/" + name + ".json");
            List<EmoteData> emotes = UniversalEmoteSerializer.readData(stream, null, "json");
            EmoteData emote = emotes.get(0);
            BetterCombatClient.emotes.put(name, emote);
            emote.isBuiltin = true;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
