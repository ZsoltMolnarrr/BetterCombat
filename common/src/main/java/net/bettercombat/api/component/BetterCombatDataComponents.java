package net.bettercombat.api.component;

import net.bettercombat.BetterCombatMod;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import java.util.function.UnaryOperator;

public class BetterCombatDataComponents {
    public static final DataComponentType<Identifier> WEAPON_PRESET_ID = register(Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "preset_id"),
            builder -> builder.persistent(Identifier.CODEC)
    );

    private static <T> DataComponentType<T> register(Identifier id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, ((DataComponentType.Builder)builderOperator.apply(DataComponentType.builder())).build());
    }
}
