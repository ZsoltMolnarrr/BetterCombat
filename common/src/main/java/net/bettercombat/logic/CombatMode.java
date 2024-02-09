package net.bettercombat.logic;

public enum CombatMode {
    BETTER_COMBAT("Better Combat server"),
    ANIMATIONS_ONLY("Vanilla server");

    private final String value;

    CombatMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
