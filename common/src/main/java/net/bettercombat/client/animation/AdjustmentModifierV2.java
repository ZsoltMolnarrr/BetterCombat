package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class AdjustmentModifierV2 extends AbstractModifier {
    public boolean enabled = true;
    protected Function<PartKey, Optional<PartModifier>> source;
    protected int instructedFadeout = 0;
    private int remainingFadeout = 0;

    public AdjustmentModifierV2(Function<PartKey, Optional<PartModifier>> source) {
        this.source = source;
    }

    protected float getFadeIn(float delta) {
        float fadeIn = 1.0F;
        IAnimation animation = this.getAnim();
        if (animation instanceof CustomAnimationPlayer player) {
            float currentTick = (float)player.getTick() + player.getTickDelta();
            fadeIn = currentTick / (float)player.getData().beginTick;
            fadeIn = Math.min(fadeIn, 1.0F);
        }

        return fadeIn;
    }

    public void tick() {
        super.tick();
        if (this.remainingFadeout > 0) {
            --this.remainingFadeout;
            if (this.remainingFadeout <= 0) {
                this.instructedFadeout = 0;
            }
        }

    }

    public void fadeOut(int fadeOut) {
        this.instructedFadeout = fadeOut;
        this.remainingFadeout = fadeOut + 1;
    }

    protected float getFadeOut(float delta) {
        float fadeOut = 1.0F;
        if (this.remainingFadeout > 0 && this.instructedFadeout > 0) {
            float current = Math.max((float)this.remainingFadeout - delta, 0.0F);
            fadeOut = current / (float)this.instructedFadeout;
            fadeOut = Math.min(fadeOut, 1.0F);
            return fadeOut;
        } else {
            IAnimation animation = this.getAnim();
            if (animation instanceof CustomAnimationPlayer player) {
                delta = player.getTickDelta(); // This is the key against stuttering
                float currentTick = (float)player.getTick() + delta;
                float fadeOutDuration = (float)(player.getData().stopTick - player.getData().endTick);

                if (fadeOutDuration > 0.0F) {
                    float position = (float)player.getData().stopTick - currentTick;
                    fadeOut = position / fadeOutDuration;
                    fadeOut = Math.min(fadeOut, 1.0F);
                }
            }

            return fadeOut;
        }
    }

    /** @deprecated */
    @Deprecated(
        forRemoval = true
    )
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        return this.get3DTransform(PartKey.keyForId(modelName), type, tickDelta, value0);
    }

    public @NotNull Vec3f get3DTransform(@NotNull PartKey partKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        if (!this.enabled) {
            return super.get3DTransform(partKey, type, tickDelta, value0);
        } else {
            Optional<PartModifier> partModifier = (Optional)this.source.apply(partKey);
            Vec3f modifiedVector = value0;

            /**
             * The key against fade causing stuttering is using `tickDelta` of the animation player
             * instead of global render tick delta.
             * Playback Speed modifiers may cause these to be different.
             */

            var fadeOut = this.getFadeOut(tickDelta);
            float fade = this.getFadeIn(tickDelta) * fadeOut;
            if (partModifier.isPresent()) {
                modifiedVector = super.get3DTransform(partKey, type, tickDelta, modifiedVector);
                var result = this.transformVector(modifiedVector, type, (PartModifier)partModifier.get(), fade);
                return result;
            } else {
                return super.get3DTransform(partKey, type, tickDelta, value0);
            }
        }
    }

    protected Vec3f transformVector(Vec3f vector, TransformType type, PartModifier partModifier, float fade) {
        switch (type) {
            case POSITION:
                return vector.add(partModifier.offset().scale(fade));
            case ROTATION:
                return vector.add(partModifier.rotation().scale(fade));
            case SCALE:
                return vector.add(partModifier.scale().scale(fade));
            case BEND:
            default:
                return vector;
        }
    }

    public static final class PartModifier {
        private final Vec3f rotation;
        private final Vec3f scale;
        private final Vec3f offset;

        public PartModifier(Vec3f rotation, Vec3f offset) {
            this(rotation, Vec3f.ZERO, offset);
        }

        public PartModifier(Vec3f rotation, Vec3f scale, Vec3f offset) {
            this.rotation = rotation;
            this.scale = scale;
            this.offset = offset;
        }

        public Vec3f rotation() {
            return this.rotation;
        }

        public Vec3f scale() {
            return this.scale;
        }

        public Vec3f offset() {
            return this.offset;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                PartModifier that = (PartModifier)obj;
                return Objects.equals(this.rotation, that.rotation) && Objects.equals(this.scale, that.scale) && Objects.equals(this.offset, that.offset);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.rotation, this.scale, this.offset});
        }

        public String toString() {
            String var10000 = String.valueOf(this.rotation);
            return "PartModifier[rotation=" + var10000 + ", scale=" + String.valueOf(this.scale) + ", offset=" + String.valueOf(this.offset) + "]";
        }
    }
}