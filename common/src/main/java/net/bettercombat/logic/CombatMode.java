package net.bettercombat.logic;

public enum CombatMode {
    ANIMATIONS_ONLY("Animations only"),
    BETTER_COMBAT("Better Combat");

    private final String value;

    CombatMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
