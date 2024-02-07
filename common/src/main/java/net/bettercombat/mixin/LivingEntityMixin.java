package net.bettercombat.mixin;

import net.bettercombat.BetterCombat;
import net.bettercombat.logic.CombatMode;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ConfigurableKnockback {

    // FEATURE: Dual wielded attacking - Client side weapon cooldown for offhand

    @Inject(method = "getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D",at = @At("HEAD"), cancellable = true)
    public void getAttributeValue_Inject(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;

        var object = (Object)this;
        if (object instanceof PlayerEntity) {
            var player = (PlayerEntity)object;
            var comboCount = ((PlayerAttackProperties)player).getComboCount();
            if (player.getWorld().isClient
                    && comboCount > 0
                    && PlayerAttackHelper.shouldAttackWithOffHand(player, comboCount)) {
                PlayerAttackHelper.offhandAttributes(player, () -> {
                    var value = player.getAttributes().getValue(attribute);
                    cir.setReturnValue(value);
                });
                cir.cancel();
            }
        }
    }

    // MARK: ConfigurableKnockback
    private float customKnockbackMultiplier_BetterCombat = 1;

    @Override
    public void setKnockbackMultiplier_BetterCombat(float value) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return;
        customKnockbackMultiplier_BetterCombat = value;
    }

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public double takeKnockback_HEAD_changeStrength(double knockbackStrength) {
        if (BetterCombat.getCurrentCombatMode() != CombatMode.BETTER_COMBAT) return knockbackStrength;
        return knockbackStrength * customKnockbackMultiplier_BetterCombat;
    }
}
