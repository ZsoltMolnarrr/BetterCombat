package net.bettercombat.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityRangeMixin {
    /**
     * This is a compatbility feature, to make
     * `getEntityInteractionRange` return a value with weapon attributes range bonus applied.
     * (Non required, to avoid crashing in case of conflict)
     */
    @WrapOperation(
            method = "getEntityInteractionRange",
            require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D")
    )
    private double getEntityInteractionRange_Wrapped_BetterCombat(PlayerEntity instance, RegistryEntry registryEntry, Operation<Double> original) {
        var originalResult = original.call(instance, registryEntry);
        return PlayerAttackHelper.getRangeWithWeapon(instance, originalResult);
    }
}
