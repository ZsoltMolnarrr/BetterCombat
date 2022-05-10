package net.bettercombat.mixin;

import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.MeleeWeaponAttributes;
import net.bettercombat.client.PlayerExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;

@Mixin(MinecraftClient.class)
public class MinecraftClientInject {

    @Shadow public ClientWorld world;

    private MinecraftClient thisClient() {
        return (MinecraftClient)((Object)this);
    }
    private boolean isHoldingAttack = false;

    // Press to attack
    @Inject(method = "doAttack",at = @At("HEAD"), cancellable = true)
    private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
        MinecraftClient client = thisClient();
        HitResult crosshairTarget = client.crosshairTarget;

        // Checking for mineable block, if not already swinging
        if (crosshairTarget != null && crosshairTarget.getType() == BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState clicked = world.getBlockState(pos);
            if (!clicked.getCollisionShape(world, pos).isEmpty() || clicked.getHardness(world, pos) != 0.0F) {
                // Mineable block found
                return;
            }
        }

        if (performAttack()) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    // Hold to attack
    @Inject(method = "handleBlockBreaking",at = @At("HEAD"), cancellable = true)
    private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
        MinecraftClient client = thisClient();
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);

            if (attributes != null) {
                boolean isPressed = client.options.attackKey.isPressed();

                HitResult crosshairTarget = client.crosshairTarget;
                // Checking for mineable block, if not already swinging
                if (isPressed && !isHoldingAttack && crosshairTarget != null && crosshairTarget.getType() == BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockState clicked = world.getBlockState(pos);
                    if (!clicked.getCollisionShape(world, pos).isEmpty() || clicked.getHardness(world, pos) != 0.0F) {
                        // Mineable block found
                        return;
                    }
                }

                if (isPressed) {
                    isHoldingAttack = true;
                    performAttack();
                    ci.cancel();
                } else {
                    isHoldingAttack = false;
                }
            }
        }
    }

    private boolean performAttack() {
        MinecraftClient client = thisClient();
        if (client.player.getMainHandStack() != null) {
            Item item = client.player.getMainHandStack().getItem();
            Identifier id = Registry.ITEM.getId(item);
            MeleeWeaponAttributes attributes = WeaponRegistry.getAttributes(id);

            if (attributes != null) {
                if (client.player.getAttackCooldownProgress(0) < 1) {
                    return true;
                }
                ((PlayerExtension) client.player).animate("slash");
                client.player.resetLastAttackedTicks();
                ((MinecraftClientAccessor) client).setAttackCooldown(10);
                return true;
            }
        }
        return false;
    }
}
