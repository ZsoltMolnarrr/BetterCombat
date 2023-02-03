package net.bettercombat.client.animation.first_person;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

import java.util.Optional;

public interface FirstPersonAnimator {
    void addFirstPersonAnimationLayer(ModifierLayer layer);
    Optional<FirstPersonAnimation> getActiveFirstPersonAnimation(float tickDelta);
}
