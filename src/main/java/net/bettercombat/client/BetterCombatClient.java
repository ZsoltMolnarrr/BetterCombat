package net.bettercombat.client;

import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigRegistry;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BetterCombatClient implements ClientModInitializer {
    private static String[] configCategory = {"client"};
    public static ClientConfig config = new ClientConfig();
    public static Config configManager = new Config(BetterCombat.MODID, configCategory, config);
    public static KeyBinding feintKeyBinding;
    public static KeyBinding toggleMineKeyBinding;

    @Override
    public void onInitializeClient() {
        configManager.load();
        ConfigRegistry.setMainConfig(configManager);

        ClientNetwork.initializeHandlers();
        WeaponAttributeTooltip.initialize();
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            var resourceManager = MinecraftClient.getInstance().getResourceManager();
            AnimationRegistry.load(resourceManager);
        });
        registerKeyBindings();

        if (FabricLoader.getInstance().isModLoaded("firstperson")) {
            FirstPersonRenderHelper.isFeatureEnabled = false;
        }
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            ConfigScreenBuilder.setMain(BetterCombat.MODID, new ClothConfigScreenBuilder());
        }

        ModelPredicateProviderRegistry.register(new Identifier(BetterCombat.MODID, "loaded"), (stack, world, entity, seed) -> {
            return 1.0F;
        });
    }

    private void registerKeyBindings() {
        feintKeyBinding = new KeyBinding(
                "keybinds.bettercombat.feint",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_R,
                "Better Combat");
        KeyBindingHelper.registerKeyBinding(feintKeyBinding);
        toggleMineKeyBinding = new KeyBinding(
                "keybinds.bettercombat.toggle_mine_with_weapons",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                "Better Combat");
        KeyBindingHelper.registerKeyBinding(toggleMineKeyBinding);
    }
}
