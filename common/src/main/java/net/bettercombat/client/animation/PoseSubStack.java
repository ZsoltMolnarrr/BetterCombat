package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PoseSubStack {
    public final MirrorModifier mirror = new MirrorModifier();
    public final ModifierLayer base = new ModifierLayer(null);
    public boolean lastAnimationUsesBodyChannel = false;
    private final boolean isMainHand;
    private final boolean isBodyChannel;
    private PoseData lastPose;
    public Consumer<KeyframeAnimation.AnimationBuilder> configure;

    public PoseSubStack(AbstractModifier adjustmentModifier, boolean isBodyChannel, boolean isMainHand) {
        this.isMainHand = isMainHand;
        this.isBodyChannel = isBodyChannel;

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
            this.base.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), null);
            lastAnimationUsesBodyChannel = false;
        } else {
            var copy = pose.mutableCopy();
            if (this.configure != null) {
                this.configure.accept(copy);
            }
            if (isBodyChannel) {
                StateCollectionHelper.configure(copy.rightItem, false, false);
                StateCollectionHelper.configure(copy.leftItem, false, false);
            } else {
                StateCollectionHelper.configure(copy.head, false, false);
                StateCollectionHelper.configure(copy.torso, false, false);
                StateCollectionHelper.configure(copy.body, false, false);
                StateCollectionHelper.configure(copy.rightArm, false, false);
                StateCollectionHelper.configure(copy.leftArm, false, false);
                StateCollectionHelper.configure(copy.rightLeg, false, false);
                StateCollectionHelper.configure(copy.leftLeg, false, false);
            }
            var animation = copy.build();
            this.mirror.setEnabled(mirror);
            this.base.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE),
                    new CustomAnimationPlayer(animation, 0));
            lastAnimationUsesBodyChannel = copy.body.isEnabled();
        }

        lastPose = newPoseData;
    }
}
