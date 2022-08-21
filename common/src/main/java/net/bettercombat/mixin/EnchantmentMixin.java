package net.bettercombat.mixin;

import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    // FEATURE: Dual wielding

    @Inject(method = "getEquipment", at = @At("RETURN"), cancellable = true)
    private void getEquipmentFix(LivingEntity entity, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        if(entity instanceof PlayerEntity player) {
            var comboCount = ((PlayerAttackProperties) player).getComboCount();
            var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
            // If striking with off-hand
            if (currentHand != null && currentHand.isOffHand()) {
                // Getting enchant from off-hand stack
                var map = cir.getReturnValue();
                if (map.get(EquipmentSlot.MAINHAND) != null) {
                    map.remove(EquipmentSlot.MAINHAND);
                }
                var offHandStack = player.getOffHandStack();
                if (!offHandStack.isEmpty()) {
                    map.put(EquipmentSlot.OFFHAND, offHandStack);
                }
                cir.setReturnValue(map);
            }
        }
    }
}
