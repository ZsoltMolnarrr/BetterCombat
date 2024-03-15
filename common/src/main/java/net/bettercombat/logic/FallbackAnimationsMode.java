package net.bettercombat.logic;

import net.minecraft.client.resource.language.I18n;

public enum FallbackAnimationsMode {
    VANILLA("vanilla"),
    ANIMATIONS_ONLY("animationsOnly");

    private final String value;

    FallbackAnimationsMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return I18n.translate("text.autoconfig.bettercombat.option.client.fallbackAnimationsMode." + value);
    }
}
