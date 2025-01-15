package net.bettercombat.api.component;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

public record WeaponAttributesIdComponent(Identifier id) {
    public static final Codec<WeaponAttributesIdComponent> CODEC = Identifier.CODEC.xmap(WeaponAttributesIdComponent::new, WeaponAttributesIdComponent::id);
}
