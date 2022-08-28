package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;

import java.util.function.Supplier;

public class EnableModifier extends AbstractModifier {
    private Supplier<Boolean> source;

    public EnableModifier(Supplier<Boolean> source) {
        this.source = source;
    }

    @Override
    public boolean isActive() {
        if (source != null) {
            return super.isActive() && source.get();
        }
        return super.isActive();
    }
}
