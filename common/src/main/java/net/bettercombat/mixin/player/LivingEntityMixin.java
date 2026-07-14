package net.bettercombat.mixin.player;

import net.bettercombat.logic.InventoryUtil;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.EquipmentSlot.OFFHAND;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ConfigurableKnockback {

    // FEATURE: Dual wielded attacking - Client side weapon cooldown for offhand

    @Inject(method = "getAttributeValue",at = @At("HEAD"), cancellable = true)
    public void getAttributeValue_Inject(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir) {
        var object = (Object)this;
        if (object instanceof Player) {
            var player = (Player)object;
            var comboCount = ((PlayerAttackProperties)player).getComboCount();
            if (player.level().isClientSide() &&
                    comboCount > 0
                    && PlayerAttackHelper.shouldAttackWithOffHand(player, comboCount)) {
                PlayerAttackHelper.swapHandAttributes(player, () -> {
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
        customKnockbackMultiplier_BetterCombat = value;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public double takeKnockback_HEAD_changeStrength(double knockbackStrength) {
        return knockbackStrength * customKnockbackMultiplier_BetterCombat;
    }


    @Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
    public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (((Object)this) instanceof Player player) {
            var mainHandHasTwoHanded = false;
            var mainHandStack = ((PlayerEntityAccessor) this).getInventory().getSelectedItem();
            var mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
            if (mainHandAttributes != null && mainHandAttributes.isTwoHanded()) {
                mainHandHasTwoHanded = true;
            }

            var offHandHasTwoHanded = false;
            var offHandStack = InventoryUtil.getOffHandSlotStack(player);
            var offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
            if(offHandAttributes != null && offHandAttributes.isTwoHanded()) {
                offHandHasTwoHanded = true;
            }

            if (slot == OFFHAND) {
                if (mainHandHasTwoHanded || offHandHasTwoHanded) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    cir.cancel();
                    return;
                }
            }
        }
    }
}
