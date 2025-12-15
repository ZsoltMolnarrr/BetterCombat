package net.bettercombat.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.bettercombat.client.misc.ItemStackViewerPlayer;
import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    @Inject(method = "appendAttributeModifiersTooltip", at = @At("HEAD"))
    private void appendAttributeModifiersTooltip_BetterCombat_HEAD(Consumer<Text> textConsumer, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ItemStackViewerPlayer viewer) {
            var itemStack = (ItemStack) (Object)this;
            viewer.betterCombat_setViewedItemStack(itemStack);
        }
    }

    @Inject(method = "appendAttributeModifiersTooltip", at = @At("TAIL"))
    private void appendAttributeModifiersTooltip_BetterCombat_TAIL(Consumer<Text> textConsumer, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ItemStackViewerPlayer viewer) {
            viewer.betterCombat_setViewedItemStack(null);
        }
    }

    @WrapOperation(method = "method_57370",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/AttributeModifiersComponent$Display;addTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V"),
            require = 0)
    private static void wrapTooltip(AttributeModifiersComponent.Display instance,
                                    Consumer<Text> textConsumer,
                                    @Nullable PlayerEntity player,
                                    RegistryEntry<EntityAttribute> attribute,
                                    EntityAttributeModifier modifier,
                                    Operation<Void> original) {
        if (BetterCombatClientMod.config.isTooltipAttackRangeReformat
                && attribute.value() == EntityAttributes.ENTITY_INTERACTION_RANGE.value()
                && player != null) { // Even vanilla code checks for this
            ItemStack itemStack = null;
            if (player instanceof ItemStackViewerPlayer viewer) {
                itemStack = viewer.betterCombat_getViewedItemStack();
            }
            if (WeaponRegistry.getAttributes(itemStack) != null                     // Only for weapons
                    && EntityAttributeHelper.rangeModifierCount(itemStack) == 1) {  // Only if there is exactly one range modifier
                var value = modifier.value() + player.getAttributeBaseValue(EntityAttributes.ENTITY_INTERACTION_RANGE);
                textConsumer.accept(WeaponAttributeTooltip.attackRangeLine(value));
                return;
            }
        }

        original.call(instance, textConsumer, player, attribute, modifier);
    }
}
