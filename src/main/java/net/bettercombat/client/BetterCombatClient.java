package net.bettercombat.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.BetterCombat;
import net.bettercombat.client.animation.FirstPersonRenderHelper;
import net.bettercombat.config.ClientConfig;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.helper.events.ClientLifecycleEvents;
import net.bettercombat.helper.events.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.ModList;

public class BetterCombatClient {
    public static ClientConfig config;
    public static KeyBinding feintKeyBinding;
    public static KeyBinding toggleMineKeyBinding;

    public void onInitializeClient() {
        AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        // Intuitive way to load a config :)
        config = AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig().client;

        ClientNetwork.initializeHandlers();
        WeaponAttributeTooltip.initialize();
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            var resourceManager = MinecraftClient.getInstance().getResourceManager();
            AnimationRegistry.load(resourceManager);
        });
        registerKeyBindings();

        if (ModList.get().isLoaded("firstperson")) {
            FirstPersonRenderHelper.isFeatureEnabled = false;
        }

        ModelPredicateProviderRegistry.registerGeneric(new Identifier(BetterCombat.MODID, "loaded"), (stack, world, entity, seed) -> {
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
