package net.bettercombat.mixin.client;

import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    @Inject(method = "appendAttributeModifierTooltip", at = @At("HEAD"), cancellable = true)
    private void appendAttributeModifierTooltip_BetterCombat_Range(Consumer<Text> textConsumer, PlayerEntity player,
                                                                   RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci) {
        if (BetterCombatClientMod.config.isTooltipAttackRangeReformat
                && attribute.value() == EntityAttributes.ENTITY_INTERACTION_RANGE.value()
                && player != null) { // Even vanilla code checks for this
            var itemStack = (ItemStack) (Object) this;
            if (WeaponRegistry.getAttributes(itemStack) != null                     // Only for weapons
                    && EntityAttributeHelper.rangeModifierCount(itemStack) == 1) {  // Only if there is exactly one range modifier
                ci.cancel();
                var value = modifier.value() + player.getAttributeBaseValue(EntityAttributes.ENTITY_INTERACTION_RANGE);
                textConsumer.accept(WeaponAttributeTooltip.attackRangeLine(value));
            }
        }
    }
}
