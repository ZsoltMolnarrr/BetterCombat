package net.bettercombat.mixin;

import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityInject {

    // FEATURE: Dual wielded attacking - Client side weapon cooldown for offhand

    @Inject(method = "getAttributeValue",at = @At("HEAD"), cancellable = true)
    public void getAttributeValue_Inject(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        var object = (Object)this;
        if (object instanceof PlayerEntity) {
            var player = (PlayerEntity)object;
            var comboCount = ((PlayerAttackProperties)player).getComboCount();
            if (player.world.isClient
                    && comboCount > 0
                    && PlayerAttackHelper.shouldAttackWithOffHand(player, comboCount)) {
                PlayerAttackHelper.setAttributesForOffHandAttack(player, true);
                var value = player.getAttributes().getValue(attribute);
                PlayerAttackHelper.setAttributesForOffHandAttack(player, false);
                cir.cancel();
                cir.setReturnValue(value);
            }
        }
    }
}
