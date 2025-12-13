package net.bettercombat.client;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.enums.PlayState;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.client.animation.AttackAnimationStack;
import net.bettercombat.client.compat.CompatibilityFlags;
import net.bettercombat.config.ClientConfig;
import net.bettercombat.config.ClientConfigWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BetterCombatClientMod {
    public static boolean ENABLED = false;
    public static ClientConfig config;

    public static void init() {
        AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        // Intuitive way to load a config :)
        config = AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig().client;

        CompatibilityFlags.initialize();

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(AttackAnimationStack.ID, 2000,
                player -> new AttackAnimationStack(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
    }
}
