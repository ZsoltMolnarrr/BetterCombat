package net.bettercombat.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerEntityRangeMixin {
    /**
     * This is a compatbility feature, to make
     * `getEntityInteractionRange` return a value with weapon attributes range bonus applied.
     * (Non required, to avoid crashing in case of conflict)
     */
    @WrapOperation(
            method = "entityInteractionRange",
            require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    )
    private double getEntityInteractionRange_Wrapped_BetterCombat(Player instance, Holder registryEntry, Operation<Double> original) {
        var originalResult = original.call(instance, registryEntry);
        return PlayerAttackHelper.getRangeWithWeapon(instance, originalResult);
    }
}
