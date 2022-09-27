package net.bettercombat.client;

import net.bettercombat.logic.WeaponRegistry;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class WeaponAttributeTooltip {
    public static void initialize() {
        ItemTooltipCallback.EVENT.register((itemStack, context, lines) -> {
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

                if (BetterCombatClient.config.isTooltipAttackRangeEnabled) {
                    var operationId = EntityAttributeModifier.Operation.ADDITION.getId();
                    var rangeTranslationKey = "attribute.name.generic.attack_range";
                    var rangeValue = attributes.attackRange();
                    var rangeLine = Text.literal(" ").append(Text.translatable("attribute.modifier.equals." + operationId, MODIFIER_FORMAT.format(rangeValue), Text.translatable(rangeTranslationKey))).formatted(Formatting.DARK_GREEN);
                    int index = lastGreenAttributeIndex != null ? lastGreenAttributeIndex : lastAttributeLine;
                    lines.add(index + 1, rangeLine);
                }

                if (attributes.isTwoHanded() && firstHandLine > 0) {
                    var handLine = Text.translatable("item.modifiers.two_handed").formatted(Formatting.GRAY);
                    lines.add(firstHandLine, handLine);
                }
            }
        });
    }
}
