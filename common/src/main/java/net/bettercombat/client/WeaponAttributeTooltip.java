package net.bettercombat.client;

import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import java.util.List;

public class WeaponAttributeTooltip {
    public static void modifyTooltip(ItemStack itemStack, List<Component> lines) {
        var attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes != null) {
            // Looking for last attribute line in the list
            var lastAttributeLine = 0;
            var firstHandLine = 0;
            Integer lastGreenAttributeIndex = null;
            var attributePrefix = "attribute.modifier";
            var attributeEqualsPrefix = "attribute.modifier.equals.0";
            var handPrefix = "item.modifiers";
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var content = line.getContents();
                // Is this a line like "+1 Something"
                if (content instanceof TranslatableContents translatableText) {
                    var key = translatableText.getKey();
                    if (key.startsWith(attributePrefix)) {
                        lastAttributeLine = i;
                    }
                    if (firstHandLine == 0 && key.startsWith(handPrefix)) {
                        firstHandLine = i;
                    }
                } else {
                    for(var part: line.getSiblings()) {
                        var partContent = part.getContents();
                        if (partContent instanceof TranslatableContents translatableText) {
                            if (translatableText.getKey().contains(attributeEqualsPrefix)) {
                                lastGreenAttributeIndex = i;
                            }
                            if (translatableText.getKey().startsWith(attributePrefix)) {
                                lastAttributeLine = i;
                            }
                        }
                    }
                }
            }

            double range = 0;
            var player = Minecraft.getInstance().player;
            if (player != null && !EntityAttributeHelper.itemHasRangeAttribute(itemStack)) {
                range = PlayerAttackHelper.getStaticRange(player, itemStack);
            }
            if (BetterCombatClientMod.config.isTooltipAttackRangeEnabled
                    && attributes.attacks() != null && attributes.attacks().length > 0
                    && range > 0) {
                var rangeLine = attackRangeLine(range);
                int index = lastGreenAttributeIndex != null ? lastGreenAttributeIndex : lastAttributeLine;
                lines.add(index + 1, rangeLine);
            }

            if (attributes.isTwoHanded() && firstHandLine > 0) {
                var handLine = Component.translatable("item.held.two_handed").withStyle(ChatFormatting.GRAY);
                lines.add(firstHandLine, handLine);
            }
        }
    }

    public static Component attackRangeLine(double range) {
        var operationId = AttributeModifier.Operation.ADD_VALUE.id();
        var rangeTranslationKey = "attribute.name.generic.attack_range";
        return CommonComponents.space()
                .append(Component.translatable("attribute.modifier.equals." + operationId,
                        new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(range),
                                Component.translatable(rangeTranslationKey)})
                ).withStyle(ChatFormatting.DARK_GREEN);
    }
}
