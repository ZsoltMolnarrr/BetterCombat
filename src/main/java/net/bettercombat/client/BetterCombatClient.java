package net.bettercombat.client;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.config.BetterCombatConfig;
import net.bettercombat.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class BetterCombatClient implements ClientModInitializer {
//    @ConfigEntry.Category("client")
//    public static ClientConfig config = new ClientConfig();
    public static ClientConfig config() {
        return AutoConfig.getConfigHolder(BetterCombatConfig.class).getConfig().client;
    }
    public static KeyBinding feintKeyBinding;
    public static KeyBinding toggleMineKeyBinding;

    @Override
    public void onInitializeClient() {
//        config.load();
        AutoConfig.register(BetterCombatConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));


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
//        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
//            ConfigScreenBuilder.setMain(BetterCombat.MODID, new ClothConfigScreenBuilder());
//        }
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
