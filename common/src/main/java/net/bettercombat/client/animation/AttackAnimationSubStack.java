package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import net.bettercombat.client.animation.modifier.TransmissionSpeedModifier;

public class AttackAnimationSubStack {
    public final TransmissionSpeedModifier speed = new TransmissionSpeedModifier();
    public final MirrorModifier mirror = new MirrorModifier();
    public final ModifierLayer base = new ModifierLayer(null);
    public final AdjustmentModifier adjustmentModifier;

    public AttackAnimationSubStack(AdjustmentModifier adjustmentModifier) {
        this.adjustmentModifier = adjustmentModifier;
        mirror.setEnabled(false);
        base.addModifier(adjustmentModifier, 0);
        base.addModifier(speed, 0);
        base.addModifier(mirror, 0);
    }
}
