package net.bettercombat.client.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.lib.mochafloats.MochaEngine;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.layered.ModifierLayer;
import com.zigythebird.playeranimcore.animation.layered.modifier.AdjustmentModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.compat.FirstPersonAnimationCompatibility;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class AttackAnimationStack extends PlayerAnimationController {

    public static final Identifier ID = Identifier.of(BetterCombatMod.ID, "attack");

    public final TransmissionSpeedModifier speed = new TransmissionSpeedModifier(1F);
    public final MirrorModifier mirror = new MirrorModifier();
    public final ModifierLayer base = new ModifierLayer(null);

    public AttackAnimationStack(AbstractClientPlayerEntity player, AnimationStateHandler animationHandler) {
        super(player, animationHandler);
        postInit();
    }

    public AttackAnimationStack(AbstractClientPlayerEntity player, AnimationStateHandler animationHandler, Function<AnimationController, MochaEngine<AnimationController>> molangRuntime) {
        super(player, animationHandler, molangRuntime);
        postInit();
    }

    public FirstPersonConfiguration activeFirstPersonConfig = new FirstPersonConfiguration();

    private void postInit() {
        // this.addModifier(base, 0);
        this.addModifier(mirror, 0);
        this.addModifier(speed, 0);
        this.addModifierLast(createAttackAdjustment());

        this.firstPersonMode = (controller) -> FirstPersonAnimationCompatibility.firstPersonMode();
        this.firstPersonConfiguration = (controller) -> {
            return this.activeFirstPersonConfig;
        };
        this.setPostAnimationSetupConsumer((func) -> {
            func.apply("torso").setEnabled(true);
            func.apply("head").rotXEnabled = false;
        });
    }

    private AdjustmentModifier createAttackAdjustment() {
        return new AdjustmentModifier((partName) -> {
            var player = this.getPlayer();
            // System.out.println("Player pitch: " + player.getPitch());
            float rotationX = 0;
            float rotationY = 0;
            float rotationZ = 0;
            float offsetX = 0;
            float offsetY = 0;
            float offsetZ = 0;

            var pitch = player.getPitch();

            if (FirstPersonMode.isFirstPersonPass()) {
                pitch = (float) Math.toRadians(pitch);
                if (partName == EntityModelPartNames.BODY) {
                    rotationX += pitch;
                    if (pitch < 0) {
                        var offset = Math.abs(Math.sin(pitch));
                        offsetY += offset * 0.5;
                        offsetZ -= offset;
                    }
                    // else if (isArm(partName)) rotationX = pitch;
                } else return Optional.empty();
            } else {
                pitch = (float) Math.toRadians(pitch);
                if (partName == EntityModelPartNames.BODY) rotationX += pitch * 0.75F;
                else if (isArm(partName)) rotationX += pitch * 0.25F;
                else if (isLeg(partName)) rotationX -= pitch * 0.75;
                else return Optional.empty();
            }

            return Optional.of(new AdjustmentModifier.PartModifier(
                    new Vec3f(rotationX, rotationY, rotationZ),
                    new Vec3f(offsetX, offsetY, offsetZ))
            );
        });
    }
    private boolean isArm(String partName) {
        return partName == EntityModelPartNames.RIGHT_ARM || partName == EntityModelPartNames.LEFT_ARM;
    }
    private boolean isLeg(String partName) {
        return partName == EntityModelPartNames.RIGHT_LEG || partName == EntityModelPartNames.LEFT_LEG;
    }
}
