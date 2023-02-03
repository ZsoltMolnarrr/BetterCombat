package net.bettercombat.client.animation.first_person;

import org.jetbrains.annotations.Nullable;

public interface FirstPersonAnimationPlayer {
    boolean isActiveInFirstPerson(float tickDelta);
    @Nullable FirstPersonAnimation.Configuration getFirstPersonPlaybackConfig();
}
