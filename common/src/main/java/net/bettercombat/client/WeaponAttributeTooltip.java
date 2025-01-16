package net.bettercombat.client;

import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

import java.util.List;

public class WeaponAttributeTooltip {
    public static void modifyTooltip(ItemStack itemStack, List<Text> lines) {
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
                var content = line.getContent();
                // Is this a line like "+1 Something"
                if (content instanceof TranslatableTextContent translatableText) {
                    var key = translatableText.getKey();
                    if (key.startsWith(attributePrefix)) {
                        lastAttributeLine = i;
                    }
                    if (firstHandLine == 0 && key.startsWith(handPrefix)) {
                        firstHandLine = i;
                    }
                } else {
                    for(var part: line.getSiblings()) {
                        var partContent = part.getContent();
                        if (partContent instanceof TranslatableTextContent translatableText) {
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
            var player = MinecraftClient.getInstance().player;
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
                var handLine = Text.translatable("item.held.two_handed").formatted(Formatting.GRAY);
                lines.add(firstHandLine, handLine);
            }
        }
    }

    public static Text attackRangeLine(double range) {
        var operationId = EntityAttributeModifier.Operation.ADD_VALUE.getId();
        var rangeTranslationKey = "attribute.name.generic.attack_range";
        return ScreenTexts.space()
                .append(Text.translatable("attribute.modifier.equals." + operationId,
                        new Object[]{AttributeModifiersComponent.DECIMAL_FORMAT.format(range),
                                Text.translatable(rangeTranslationKey)})
                ).formatted(Formatting.DARK_GREEN);
    }
}
