package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;

public class AttackAnimationSubStack {
    public final TransmissionSpeedModifier speed = new TransmissionSpeedModifier();
    public final MirrorModifier mirror = new MirrorModifier();
    public final ModifierLayer base = new ModifierLayer(null);

    public AttackAnimationSubStack(AbstractModifier adjustmentModifier) {
        mirror.setEnabled(false);
        base.addModifier(adjustmentModifier, 0);
        base.addModifier(speed, 0);
        base.addModifier(mirror, 0);
    }
}
