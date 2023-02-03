package net.bettercombat.client.animation.first_person;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import org.jetbrains.annotations.Nullable;

public class CustomAnimationPlayer extends KeyframeAnimationPlayer implements FirstPersonAnimationPlayer {

    /**
     * @param emote emote to play
     * @param t     begin playing from tick
     */
    public CustomAnimationPlayer(KeyframeAnimation emote, int t) {
        super(emote, t);
    }
    public enum FirstPersonPlaybackMode { NONE, COMBAT }
    private FirstPersonPlaybackMode firstPersonMode = FirstPersonPlaybackMode.NONE;
    @Nullable private FirstPersonAnimation.Configuration firstPersonConfig = null;
    public void playInFirstPersonAsCombat(FirstPersonAnimation.Configuration firstPersonConfig) {
        this.firstPersonMode = FirstPersonPlaybackMode.COMBAT;
        this.firstPersonConfig = firstPersonConfig;
    }

    public @Nullable FirstPersonAnimation.Configuration getFirstPersonPlaybackConfig() {
        return firstPersonConfig;
    }

    public boolean isWindingDown(float tickDelta) {
        var windDownStart = getData().endTick + ((getData().stopTick - getData().endTick) / 4);
        return ((getTick() + tickDelta) > (windDownStart + 0.5F)); // + 0.5 for smoother transition
    }

    @Override
    public boolean isActiveInFirstPerson(float tickDelta) {
        switch (firstPersonMode) {
            case NONE -> {
                return false;
            }
            case COMBAT -> {
                return isActive() && !isWindingDown(tickDelta);
            }
        }
        assert true;
        return false;
    }
}
