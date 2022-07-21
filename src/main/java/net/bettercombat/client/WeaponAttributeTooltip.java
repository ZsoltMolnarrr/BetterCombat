package net.bettercombat.client;

import net.bettercombat.logic.WeaponRegistry;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
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
                    if (line instanceof TranslatableText translatableText) {
                        if (translatableText.getKey().startsWith(attributePrefix)) {
                            lastAttributeLine = i;
                        }
                    } else {
                        for(var part: line.getSiblings()) {
                            if (part instanceof TranslatableText translatableText) {
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
                    var rangeLine = new LiteralText(" ").append(new TranslatableText("attribute.modifier.equals." + operationId, MODIFIER_FORMAT.format(rangeValue), new TranslatableText(rangeTranslationKey))).formatted(Formatting.DARK_GREEN);
                    lines.add(lastAttributeLine + 1, rangeLine);
                }
            }
        });
    }
}
