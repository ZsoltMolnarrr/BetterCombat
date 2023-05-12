package net.bettercombat.client.animation.modifier;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;

import java.util.Optional;
import java.util.function.Function;

public class HarshAdjustmentModifier extends AdjustmentModifier {
    public HarshAdjustmentModifier(Function<String, Optional<PartModifier>> source) {
        super(source);
    }

    @Override
    protected Vec3f transformVector(Vec3f vector, TransformType type, PartModifier partModifier, float fade) {
        switch (type) {
            case POSITION:
                return vector.add(partModifier.offset());
            case ROTATION:
                return vector.add(partModifier.rotation());
            case BEND:
                break;
        }
        return vector;
    }
}
