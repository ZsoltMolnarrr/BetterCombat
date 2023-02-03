package net.bettercombat.client.animation.first_person;

import dev.kosmx.playerAnim.api.layered.IAnimation;

public record FirstPersonAnimation(IAnimation animation, Configuration config) {
    public record Configuration(boolean showRightArm, boolean showLeftArm,
                                boolean showRightItem, boolean showLeftItem) {}
}