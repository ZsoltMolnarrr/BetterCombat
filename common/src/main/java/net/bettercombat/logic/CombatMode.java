package net.bettercombat.logic;

import net.minecraft.client.resource.language.I18n;

public enum CombatMode {
    BETTER_COMBAT("betterCombat"),
    VANILLA_SERVER("vanillaServer");

    private final String value;

    CombatMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return I18n.translate("text.autoconfig.bettercombat.option.client.singlePlayerCombatMode." + value);
    }
}
