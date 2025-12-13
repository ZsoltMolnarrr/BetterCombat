package net.bettercombat.client;

import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.client.animation.AttackAnimationStack;
import net.bettercombat.client.animation.PoseAnimationStack;
import net.bettercombat.client.compat.CompatibilityFlags;
import net.bettercombat.config.ClientConfig;
import net.bettercombat.config.ClientConfigWrapper;

public class BetterCombatClientMod {
    public static boolean ENABLED = false;
    public static ClientConfig config;

    public static void init() {
        AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        // Intuitive way to load a config :)
        config = AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig().client;

        CompatibilityFlags.initialize();
    }

    public static void setupAnimations() {
        // Attack animation (priority 2000 - highest)
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(AttackAnimationStack.ID, 2000,
                player -> new AttackAnimationStack(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );

        // Pose animations (priorities 1-4 - lower than attacks)
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PoseAnimationStack.OFF_HAND_ITEM_ID, 1,
                player -> new PoseAnimationStack(player,
                        (controller, state, animSetter) -> PlayState.STOP,
                        false, false
                )
        );

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PoseAnimationStack.OFF_HAND_BODY_ID, 2,
                player -> new PoseAnimationStack(player,
                        (controller, state, animSetter) -> PlayState.STOP,
                        true, false
                )
        );

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PoseAnimationStack.MAIN_HAND_ITEM_ID, 3,
                player -> new PoseAnimationStack(player,
                        (controller, state, animSetter) -> PlayState.STOP,
                        false, true
                )
        );

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PoseAnimationStack.MAIN_HAND_BODY_ID, 4,
                player -> new PoseAnimationStack(player,
                        (controller, state, animSetter) -> PlayState.STOP,
                        true, true
                )
        );
    }
}
