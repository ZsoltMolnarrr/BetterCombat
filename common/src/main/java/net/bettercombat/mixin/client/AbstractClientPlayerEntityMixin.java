package net.bettercombat.mixin.client;

import com.mojang.authlib.GameProfile;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.animation.*;
import net.bettercombat.client.animation.TransmissionSpeedModifier;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements PlayerAttackAnimatable {
    private AttackAnimationStack attackAnimation;
    private PoseAnimationStack mainHandBodyPose;
    private PoseAnimationStack mainHandItemPose;
    private PoseAnimationStack offHandBodyPose;
    private PoseAnimationStack offHandItemPose;

    public AbstractClientPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        var player = (AbstractClientPlayerEntity) ((Object) this);

        // Initialize attack animation
        attackAnimation = (AttackAnimationStack) PlayerAnimationAccess.getPlayerAnimationLayer(player, AttackAnimationStack.ID);
        mainHandBodyPose = (PoseAnimationStack) PlayerAnimationAccess.getPlayerAnimationLayer(player, PoseAnimationStack.MAIN_HAND_BODY_ID);
        mainHandItemPose = (PoseAnimationStack) PlayerAnimationAccess.getPlayerAnimationLayer(player, PoseAnimationStack.MAIN_HAND_ITEM_ID);
        offHandBodyPose = (PoseAnimationStack) PlayerAnimationAccess.getPlayerAnimationLayer(player, PoseAnimationStack.OFF_HAND_BODY_ID);
        offHandItemPose = (PoseAnimationStack) PlayerAnimationAccess.getPlayerAnimationLayer(player, PoseAnimationStack.OFF_HAND_ITEM_ID);
    }

    @Override
    public void updateAnimationsOnTick() {
        var instance = (Object)this;
        var player = (PlayerEntity)instance;
        var isLeftHanded = isLeftHanded();
        var hasActiveAttackAnimation = attackAnimation.isActive(); // attackAnimation.base.getAnimation() != null && attackAnimation.base.getAnimation().isActive();
        var mainHandStack = player.getMainHandStack();
        // No pose during special activities

        if (scheduledParticles != null && scheduledParticles.time() == player.age) {
            SlashParticleUtil.spawnParticles(scheduledParticles.args());
            scheduledParticles = null;
        }

        if (player.handSwinging // Official mapping name: `isHandBusy`
                || player.isSwimming()
                || player.isUsingItem()
                || player.isClimbing()
                || player.isGliding()
                || Platform.isCastingSpell(player)
                || CrossbowItem.isCharged(mainHandStack)) {
            // Clear all poses during special activities
            mainHandBodyPose.setPose(null, isLeftHanded);
            mainHandItemPose.setPose(null, isLeftHanded);
            offHandBodyPose.setPose(null, isLeftHanded);
            offHandItemPose.setPose(null, isLeftHanded);
            return;
        }

        // Restore auto body rotation upon swing - Fix issue #11
        if (hasActiveAttackAnimation) {
            ((LivingEntityAccessor)player).invokeTurnHead(player.getHeadYaw());
        }

        // Pose animations
        var betterCombatPlayer = (EntityPlayer_BetterCombat)player;

        String newMainHandPoseId = null;
        String newOffHandPoseId = null;

        if (MinecraftClient.getInstance().player == player) {
            // Logic on local player too for improved responsiveness
            var pose = PlayerAttackHelper.poseForPlayer(player);
            if (!pose.base().isEmpty()) {
                newMainHandPoseId = pose.base();
            }
            if (!pose.offHand().isEmpty()) {
                newOffHandPoseId = pose.offHand();
            }
        } else {
            // For other players, use synced animation IDs
            if (betterCombatPlayer.getMainHandIdleAnimation() != null && !betterCombatPlayer.getMainHandIdleAnimation().isEmpty()) {
                newMainHandPoseId = betterCombatPlayer.getMainHandIdleAnimation();
            }
            if (betterCombatPlayer.getOffHandIdleAnimation() != null && !betterCombatPlayer.getOffHandIdleAnimation().isEmpty()) {
                newOffHandPoseId = betterCombatPlayer.getOffHandIdleAnimation();
            }
        }

        // Update item poses (always active when pose is set)
        mainHandItemPose.setPose(newMainHandPoseId, isLeftHanded);
        offHandItemPose.setPose(newOffHandPoseId, isLeftHanded);

        // Update body poses (disabled during walking/sneaking for non-two-handed weapons)
        if (!PlayerAttackHelper.isTwoHandedWielding(player)) {
            if (this.isWalking() || this.isSneaking()) {
                newMainHandPoseId = null;
                newOffHandPoseId = null;
            }
        }
        mainHandBodyPose.setPose(newMainHandPoseId, isLeftHanded);
        offHandBodyPose.setPose(newOffHandPoseId, isLeftHanded);
    }

    @Override
    public void playAttackAnimation(String name, AnimatedHand animatedHand, float length, float upswing) {
        try {
            var controller = attackAnimation;
            var animation = PlayerAnimResources.getAnimation(Identifier.of(name));

            var endTick = animation.data().<Float>get(ExtraAnimationData.END_TICK_KEY).orElse(animation.length());
            var speed = endTick / length;
            var mirror = animatedHand.isOffHand();
            if(isLeftHanded()) {
                mirror = !mirror;
            }
            var trueUpswingRatio = upswing / BetterCombatMod.config.getUpswingMultiplier();
            float upswingSpeed = speed / trueUpswingRatio;
            float downwindSpeed = (float) (speed *
                    MathHelper.lerp(Math.max(BetterCombatMod.config.getUpswingMultiplier() - 0.5, 0) / 0.5, // Choosing value :D
                            (1F - upswing),                     // Use this value at config `0.5`
                            upswing / (1F - upswing)));         // Use this value at config `1.0`

            var fistPersonConfig = firstPersonConfig(animatedHand);
            if (animatedHand == AnimatedHand.OFF_HAND) {
                fistPersonConfig = FirstPersonHelper.mirrored(fistPersonConfig);
            }
            controller.activeFirstPersonConfig = fistPersonConfig;
            controller.speed.speed = speed;
            controller.mirror.enabled = mirror;
            attackAnimation.speed.set(upswingSpeed,
                    List.of(
                            new TransmissionSpeedModifier.Gear(length * upswing, downwindSpeed),
                            new TransmissionSpeedModifier.Gear(length, speed)
                    ));

            controller.triggerAnimation(animation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable private SlashParticleUtil.ScheduledSpawnArgs scheduledParticles = null;

    @Override
    public void playAttackParticles(boolean isOffHand, float weaponRange, int delay, List<ParticlePlacement> particles, TrailAppearance appearance) {
        var player = (AbstractClientPlayerEntity)(Object)this;
        var spawn = new SlashParticleUtil.SpawnArgs(
                player,
                isOffHand,
                weaponRange,
                particles,
                appearance
        );
        scheduledParticles = new SlashParticleUtil.ScheduledSpawnArgs(
                spawn,
                player.age + delay
        );
    }

    private boolean isWalking() {
        return !this.isDead() && (this.isSwimming() || this.getVelocity().horizontalLength() > 0.03);
    }

    public boolean isLeftHanded() {
        return this.getMainArm() == Arm.LEFT;
    }

    // PlayerAttackAnimatable

    @Override
    public void stopAttackAnimation(float length) {
        scheduledParticles = null;
        if (attackAnimation.isActive()) {
            attackAnimation.stop();
        }
    }

    // FirstPersonAnimator

    private FirstPersonConfiguration firstPersonConfig(AnimatedHand animatedHand) {
        // boolean leftHanded = getMainArm() == Arm.LEFT;
        var showRightItem = true;
        var showLeftItem = BetterCombatClientMod.config.isShowingOtherHandFirstPerson || animatedHand == AnimatedHand.TWO_HANDED;
        var showRightArm = showRightItem && BetterCombatClientMod.config.isShowingArmsInFirstPerson;
        var showLeftArm = showLeftItem && BetterCombatClientMod.config.isShowingArmsInFirstPerson;

        var config = new FirstPersonConfiguration(showRightArm, showLeftArm, showRightItem, showLeftItem);
        // System.out.println("Animation config: " + config);
        return config;
    }
}
