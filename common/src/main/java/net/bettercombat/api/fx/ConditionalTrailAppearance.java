package net.bettercombat.api.fx;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Wrapper for TrailAppearance that supports conditional appearances based on ItemStack predicates.
 * Conditions are checked in insertion order (LinkedHashMap). The first matching condition's appearance is used.
 */
public record ConditionalTrailAppearance(
        TrailAppearance default_appearance,
        LinkedHashMap<String, TrailAppearance> conditional
) {
    /**
     * Creates a ConditionalTrailAppearance with only a default appearance and no conditions.
     */
    public ConditionalTrailAppearance(TrailAppearance default_appearance) {
        this(default_appearance, new LinkedHashMap<>());
    }

    /**
     * Creates a ConditionalTrailAppearance with the default TrailAppearance and no conditions.
     */
    public ConditionalTrailAppearance() {
        this(TrailAppearance.DEFAULT, new LinkedHashMap<>());
    }

    /**
     * Gets the appropriate trail appearance for the given ItemStack.
     * Checks conditions in order and returns the first matching appearance.
     * If no conditions match, returns the default appearance.
     *
     * @param itemStack The ItemStack to check conditions against
     * @return The appropriate TrailAppearance
     */
    @Nullable
    public TrailAppearance resolve(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return default_appearance;
        }

        // Check conditional appearances in order
        for (Map.Entry<String, TrailAppearance> entry : conditional.entrySet()) {
            String conditionId = entry.getKey();
            if (ItemConditions.test(conditionId, itemStack)) {
                return entry.getValue();
            }
        }

        // Return default if no conditions matched
        return default_appearance;
    }

    /**
     * Merges this ConditionalTrailAppearance with another, creating a new instance.
     * The override's values take precedence over the base's values.
     *
     * @param override The ConditionalTrailAppearance to merge with this one (takes precedence)
     * @return A new merged ConditionalTrailAppearance
     */
    public ConditionalTrailAppearance merge(@Nullable ConditionalTrailAppearance override) {
        if (override == null) {
            return this;
        }

        // Override's default_appearance takes precedence if not null
        TrailAppearance mergedDefault = override.default_appearance != null
                ? override.default_appearance
                : this.default_appearance;

        // Merge conditional maps: base entries first, then override entries (which replace matching keys)
        LinkedHashMap<String, TrailAppearance> mergedConditional = new LinkedHashMap<>(this.conditional);
        mergedConditional.putAll(override.conditional);

        return new ConditionalTrailAppearance(mergedDefault, mergedConditional);
    }
}
