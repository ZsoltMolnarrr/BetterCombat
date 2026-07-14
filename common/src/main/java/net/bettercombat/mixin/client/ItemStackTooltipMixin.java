package net.bettercombat.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.bettercombat.client.misc.ItemStackViewerPlayer;
import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    @Inject(method = "addAttributeTooltips", at = @At("HEAD"))
    private void appendAttributeModifiersTooltip_BetterCombat_HEAD(Consumer<Component> textConsumer, TooltipDisplay displayComponent, @Nullable Player player, CallbackInfo ci) {
        if (player instanceof ItemStackViewerPlayer viewer) {
            var itemStack = (ItemStack) (Object)this;
            viewer.betterCombat_setViewedItemStack(itemStack);
        }
    }

    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void appendAttributeModifiersTooltip_BetterCombat_TAIL(Consumer<Component> textConsumer, TooltipDisplay displayComponent, @Nullable Player player, CallbackInfo ci) {
        if (player instanceof ItemStackViewerPlayer viewer) {
            viewer.betterCombat_setViewedItemStack(null);
        }
    }

    @WrapOperation(method = "method_57370",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers$Display;apply(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"),
            require = 0)
    private static void wrapTooltip(ItemAttributeModifiers.Display instance,
                                    Consumer<Component> textConsumer,
                                    @Nullable Player player,
                                    Holder<Attribute> attribute,
                                    AttributeModifier modifier,
                                    Operation<Void> original) {
        if (BetterCombatClientMod.config.isTooltipAttackRangeReformat
                && attribute.value() == Attributes.ENTITY_INTERACTION_RANGE.value()
                && player != null) { // Even vanilla code checks for this
            ItemStack itemStack = null;
            if (player instanceof ItemStackViewerPlayer viewer) {
                itemStack = viewer.betterCombat_getViewedItemStack();
            }
            if (WeaponRegistry.getAttributes(itemStack) != null                     // Only for weapons
                    && EntityAttributeHelper.rangeModifierCount(itemStack) == 1) {  // Only if there is exactly one range modifier
                var value = modifier.amount() + player.getAttributeBaseValue(Attributes.ENTITY_INTERACTION_RANGE);
                textConsumer.accept(WeaponAttributeTooltip.attackRangeLine(value));
                return;
            }
        }

        original.call(instance, textConsumer, player, attribute, modifier);
    }
}
