package net.bettercombat.mixin.player;

import net.bettercombat.logic.InventoryUtil;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.entity.EquipmentSlot.OFFHAND;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ConfigurableKnockback {

    // FEATURE: Dual wielded attacking - Client side weapon cooldown for offhand

    @Inject(method = "getAttributeValue",at = @At("HEAD"), cancellable = true)
    public void getAttributeValue_Inject(RegistryEntry<EntityAttribute> attribute, CallbackInfoReturnable<Double> cir) {
        var object = (Object)this;
        if (object instanceof PlayerEntity) {
            var player = (PlayerEntity)object;
            var comboCount = ((PlayerAttackProperties)player).getComboCount();
            if (player.getEntityWorld().isClient() &&
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

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public double takeKnockback_HEAD_changeStrength(double knockbackStrength) {
        return knockbackStrength * customKnockbackMultiplier_BetterCombat;
    }


    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (((Object)this) instanceof PlayerEntity player) {
            var mainHandHasTwoHanded = false;
            var mainHandStack = ((PlayerEntityAccessor) this).getInventory().getSelectedStack();
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
