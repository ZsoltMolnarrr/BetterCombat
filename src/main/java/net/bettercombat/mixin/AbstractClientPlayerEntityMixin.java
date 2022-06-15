package net.bettercombat.mixin;

import com.mojang.authlib.GameProfile;
import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.playerAnim.IAnimatedPlayer;
import io.github.kosmx.playerAnim.layered.AnimationContainer;
import io.github.kosmx.playerAnim.layered.EmoteDataPlayer;
import io.github.kosmx.playerAnim.layered.IAnimation;
import net.bettercombat.attack.WeaponRegistry;
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

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements PlayerAttackAnimatable {

    private AnimationContainer pose = new AnimationContainer(null);
    private AnimationContainer attack = new AnimationContainer(null);

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        ((IAnimatedPlayer) this)
                .getAnimationStack()
                .addAnimLayer(1, pose);
        ((IAnimatedPlayer) this)
                .getAnimationStack()
                .addAnimLayer(2000, attack);
    }

    private EmoteData lastPose;

    @Override
    public void updatePose() {
        var instance = (Object)this;
        var player = (PlayerEntity)instance;
        var attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        EmoteData newPose = null;
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
            this.pose.setAnim(null);
        } else {
            this.pose.setAnim(new EmoteDataPlayer(pose, 0));
        }

        lastPose = pose;
    }

    @Override
    public void playAttackAnimation(String name, boolean isOffHand) {
        try {
            EmoteData data = AnimationRegistry.emotes.get(name);
            attack.setAnim(new EmoteDataPlayer(data, 0));
            this.bodyYaw = this.headYaw;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopAttackAnimation() {
        IAnimation currentAnimation = attack.getAnim();
        if (currentAnimation != null && currentAnimation instanceof EmoteDataPlayer) {
            ((EmoteDataPlayer)currentAnimation).stop();
        }
    }
}
