package net.bettercombat.mixin;

import com.mojang.authlib.GameProfile;
import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.playerAnim.IAnimatedPlayer;
import io.github.kosmx.playerAnim.layered.AnimationContainer;
import io.github.kosmx.playerAnim.layered.EmoteDataPlayer;
import io.github.kosmx.playerAnim.layered.IAnimation;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.client.AnimationRegistry;
import net.bettercombat.client.PlayerAttackAnimatable;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements PlayerAttackAnimatable {

    private AnimationContainer posePlayer = new AnimationContainer(null);
    private AnimationContainer attackPlayer = new AnimationContainer(null);
    private EmoteData lastPose;

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        ((IAnimatedPlayer) this)
                .getAnimationStack()
                .addAnimLayer(1, posePlayer);
        ((IAnimatedPlayer) this)
                .getAnimationStack()
                .addAnimLayer(2000, attackPlayer);
    }

    @Override
    public void updateAnimationsOnTick() {
        var instance = (Object)this;
        var player = (PlayerEntity)instance;
        EmoteData newPose = null;
        if (player.handSwinging) {
            setPose(newPose);
            return;
        }
        var attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        if (attributes != null && attributes.pose() != null) {
            newPose = AnimationRegistry.emotes.get(attributes.pose());
        }
        setPose(newPose);
    }

    private void setPose(@Nullable EmoteData pose) {
        if (pose == lastPose) {
            return;
        }

        if (pose == null) {
            this.posePlayer.setAnim(null);
        } else {
            var copy = pose.mutableCopy().build();
            updateAnimationByCurrentActivity(copy);
            this.posePlayer.setAnim(new EmoteDataPlayer(copy, 0));
        }

        lastPose = pose;
    }

    @Override
    public void playAttackAnimation(String name, boolean isOffHand) {
        try {
            EmoteData animation = AnimationRegistry.emotes.get(name).mutableCopy().build();
            updateAnimationByCurrentActivity(animation);
            attackPlayer.setAnim(new EmoteDataPlayer(animation, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAnimationByCurrentActivity(EmoteData animation) {
        var pose = getPose();
        switch (pose) {
            case STANDING -> {
            }
            case FALL_FLYING -> {
            }
            case SLEEPING -> {
            }
            case SWIMMING -> {
                configurBodyPart(animation.bodyParts.get("rightLeg"), false, false);
                configurBodyPart(animation.bodyParts.get("leftLeg"), false, false);
            }
            case SPIN_ATTACK -> {
            }
            case CROUCHING -> {
                configurBodyPart(animation.bodyParts.get("head"), true, false);
                configurBodyPart(animation.bodyParts.get("rightArm"), true, false);
                configurBodyPart(animation.bodyParts.get("leftArm"), true, false);
                configurBodyPart(animation.bodyParts.get("rightLeg"), false, false);
                configurBodyPart(animation.bodyParts.get("leftLeg"), false, false);
            }
            case LONG_JUMPING -> {
            }
            case DYING -> {
            }
        }
        if (isMounting()) {
            configurBodyPart(animation.bodyParts.get("rightLeg"), false, false);
            configurBodyPart(animation.bodyParts.get("leftLeg"), false, false);
        }
    }

    private static void configurBodyPart(EmoteData.StateCollection bodyPart, boolean isRotationEnabled, boolean isOffsetEnabled) {
        bodyPart.pitch.isEnabled = isRotationEnabled;
        bodyPart.roll.isEnabled = isRotationEnabled;
        bodyPart.yaw.isEnabled = isRotationEnabled;
        bodyPart.x.isEnabled = isOffsetEnabled;
        bodyPart.y.isEnabled = isOffsetEnabled;
        bodyPart.z.isEnabled = isOffsetEnabled;
    }

    @Override
    public void stopAttackAnimation() {
        IAnimation currentAnimation = attackPlayer.getAnim();
        if (currentAnimation != null && currentAnimation instanceof EmoteDataPlayer) {
            ((EmoteDataPlayer)currentAnimation).stop();
        }
    }

    private boolean isMounting() {
        return this.getVehicle() != null;
    }
}
