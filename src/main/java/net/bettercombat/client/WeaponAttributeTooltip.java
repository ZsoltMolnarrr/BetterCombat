package net.bettercombat.client;

import net.bettercombat.logic.WeaponRegistry;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.MutableText;
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
                var attributePrefix = "attribute.modifier";
                for (int i = 0; i < lines.size(); i++) {
                    var line = lines.get(i);
                    // Is this a line like "+1 Something"
                    if (line.getContent() instanceof TranslatableTextContent translatableText) {
                        if (translatableText.getKey().startsWith(attributePrefix)) {
                            lastAttributeLine = i;
                        }
                    } else {
                        for(var part: line.getSiblings()) {
                            if (part.getContent() instanceof TranslatableTextContent translatableText) {
                                if (translatableText.getKey().startsWith(attributePrefix)) {
                                    lastAttributeLine = i;
                                    break;
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
                    lines.add(lastAttributeLine + 1, rangeLine);
                }
            }
        });
    }
}
