package net.bettercombat.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BetterCombatClient implements ClientModInitializer {
    private static String[] configCategory = {"client"};
    public static ClientConfig config = new ClientConfig();
    private static Config settings = new Config(BetterCombat.MODID, configCategory, config);
    public static KeyBinding feintKeyBinding;

    @Override
    public void onInitializeClient() {
        ClientNetwork.initializeHandlers();
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            AnimationRegistry.load(MinecraftClient.getInstance().getResourceManager());
        });
        settings.load();
        ConfigRegistry.setMainConfig(settings);
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            ConfigScreenBuilder.setMain(BetterCombat.MODID, new ClothConfigScreenBuilder());
        }
        registerKeyBindings();
        registerSounds();
    }

    private void registerKeyBindings() {
        feintKeyBinding = new KeyBinding(
                "config.bettercombat.clientConfig.feintKey",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_R,
                "Better Combat");
        KeyBindingHelper.registerKeyBinding(feintKeyBinding);
    }

    private void registerSounds() {
        var url = BetterCombatClient.class.getResource("/assets/" + BetterCombat.MODID + "/sounds.json");
        var filePath = url.getPath();

        try {
            JsonReader reader = new JsonReader(new FileReader(filePath));
            var gson = new Gson();
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> sounds = gson.fromJson(reader, mapType);
            sounds.forEach((key, value) -> {
                var soundId = new Identifier(BetterCombat.MODID, key);
                var soundEvent = new SoundEvent(soundId);
                Registry.register(Registry.SOUND_EVENT, soundId, soundEvent);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
