package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PoseSubStack {

    public final MirrorModifier mirror = new MirrorModifier();
    public final ModifierLayer base = new ModifierLayer(null);
    private final boolean isMainHand;
    private PoseData lastPose;
    public Consumer<KeyframeAnimation.AnimationBuilder> configure;

    public PoseSubStack(AbstractModifier adjustmentModifier, boolean isMainHand) {
        this.isMainHand = isMainHand;

        if (adjustmentModifier != null) {
            base.addModifier(adjustmentModifier, 0);
        }
        base.addModifier(mirror, 0);
    }

    public void setPose(@Nullable KeyframeAnimation pose, boolean isLeftHanded) {
        var mirror = isLeftHanded;
        if (!isMainHand) {
            mirror = !mirror;
        }
        var newPoseData = PoseData.from(pose, mirror);
        if (lastPose != null && newPoseData.equals(lastPose)) {
            return;
        }

        if (pose == null) {
            this.base.setAnimation(null);
        } else {
            var copy = pose.mutableCopy();
            this.configure.accept(copy);
            this.mirror.setEnabled(mirror);
            this.base.setAnimation(new KeyframeAnimationPlayer(copy.build(), 0));
        }

        lastPose = newPoseData;
    }
}
