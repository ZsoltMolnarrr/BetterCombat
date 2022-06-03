package net.bettercombat.mixin;

import com.mojang.authlib.GameProfile;
import io.github.kosmx.emotes.common.emote.EmoteData;
import io.github.kosmx.playerAnim.IAnimatedPlayer;
import io.github.kosmx.playerAnim.layered.AnimationContainer;
import io.github.kosmx.playerAnim.layered.EmoteDataPlayer;
import io.github.kosmx.playerAnim.layered.IAnimation;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.PlayerExtension;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class ClientPlayerMixin extends PlayerEntity implements PlayerExtension {

    private AnimationContainer container = new AnimationContainer(null);

    public ClientPlayerMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        ((IAnimatedPlayer) this)
                .getAnimationStack()
                .addAnimLayer(2000, container);
    }

    @Override
    public void playAttackAnimation(String name, boolean isOffHand) {
        EmoteData data = BetterCombatClient.emotes.get(name);
        container.setAnim(new EmoteDataPlayer(data, 0));
        this.bodyYaw = this.headYaw;
    }

    @Override
    public void stopAnimation() {
        IAnimation currentAnimation = container.getAnim();
        if (currentAnimation != null && currentAnimation instanceof EmoteDataPlayer) {
            ((EmoteDataPlayer)currentAnimation).stop();
        }
        // container.setAnim(null);
    }
}
