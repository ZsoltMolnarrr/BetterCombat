package net.bettercombat.client;

import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigRegistry;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.bettercombat.BetterCombat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BetterCombatClient implements ClientModInitializer {
    public static Map<String, EmoteData> emotes = new HashMap<>();
    private static String[] configCategory = {"client"};
    public static ClientConfig config = new ClientConfig();
    private static Config settings = new Config(BetterCombat.MODID, configCategory, config);
    public static KeyBinding feintKeyBinding;

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            loadAnimations("slash");
        });
        settings.load();
        ConfigRegistry.setMainConfig(settings);
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            ConfigScreenBuilder.setMain(BetterCombat.MODID, new ClothConfigScreenBuilder());
        }
        feintKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                config.feintKey.getTranslationKey(),
                config.feintKey.getCategory(),
                config.feintKey.getCode(),
                BetterCombat.MODID));
        config.listener = new ClientConfig.Listener() {
            @Override
            public void feintKeyUpdated() {
                feintKeyBinding.setBoundKey(config.feintKey);
            }
        };
    }

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
