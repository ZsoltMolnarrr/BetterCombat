package net.bettercombat.api.component;

import net.bettercombat.BetterCombatMod;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class BetterCombatDataComponents {
    public static final ComponentType<Identifier> WEAPON_PRESET_ID = register(Identifier.of(BetterCombatMod.ID, "preset_id"),
            builder -> builder.codec(Identifier.CODEC)
    );

    private static <T> ComponentType<T> register(Identifier id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
    }
}
