package net.bettercombat.client.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.layered.ModifierLayer;
import com.zigythebird.playeranimcore.animation.layered.modifier.AdjustmentModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.compat.FirstPersonAnimationCompatibility;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Pose;
import java.util.Optional;

public class AttackAnimationStack extends PlayerAnimationController {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "attack");

    public final TransmissionSpeedModifier speed = new TransmissionSpeedModifier(1F);
    public final MirrorModifier mirror = new MirrorModifier();
    public final ModifierLayer base = new ModifierLayer(null);

    public AttackAnimationStack(Avatar entity, AnimationStateHandler animationHandler) {
        super(entity, animationHandler);
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

            // Disable leg animations based on player activity
            var player = this.getAvatar();
            var pose = player.getPose();
            boolean disableLegs = false;

            // Swimming: disable legs
            if (pose == Pose.SWIMMING) {
                disableLegs = true;
            }

            // Mounting/riding: disable legs
            if (player.getVehicle() != null) {
                disableLegs = true;
            }

            // Fast movement: optionally disable legs based on config
            if (!disableLegs) {
                var legAnimationThreshold = BetterCombatClientMod.config.legAnimationThreshold;
                if (legAnimationThreshold > 0) {
                    var moving = player.isSprinting() || isWalking(player);
                    if (moving && player.getDeltaMovement().horizontalDistanceSqr() > (legAnimationThreshold * legAnimationThreshold)) {
                        disableLegs = true;
                    }
                }
            }

            if (disableLegs) {
                func.apply(PartNames.RIGHT_LEG).setEnabled(false);
                func.apply(PartNames.LEFT_LEG).setEnabled(false);
            }
        });
    }

    private AdjustmentModifier createAttackAdjustment() {
        return new AdjustmentModifier((partName, data) -> {
            var player = this.getAvatar();
            // System.out.println("Player pitch: " + player.getPitch());
            float rotationX = 0;
            float rotationY = 0;
            float rotationZ = 0;
            float offsetX = 0;
            float offsetY = 0;
            float offsetZ = 0;

            var pitch = player.getXRot();

            if (data.isFirstPersonPass()) {
                pitch = (float) Math.toRadians(pitch);
                if (partName.equals(PartNames.BODY)) {
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
                if (partName.equals(PartNames.BODY)) rotationX += pitch * 0.75F;
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
        return partName.equals(PartNames.RIGHT_ARM) || partName.equals(PartNames.LEFT_ARM);
    }
    private boolean isLeg(String partName) {
        return partName.equals(PartNames.RIGHT_LEG) || partName.equals(PartNames.LEFT_LEG);
    }

    private static boolean isWalking(Avatar player) {
        return !player.isDeadOrDying() && (player.isSwimming() || player.getDeltaMovement().horizontalDistance() > 0.03);
    }
}
