package net.bettercombat.api.fx;

import net.bettercombat.BetterCombatMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows adjusting the trail appearance of a weapon swing, based on the attacking player.
 * Unlike {@link ItemConditions}, this receives the attacker, so it can depend on status effects and such.
 * Runs on the attacker's client, and the result is sent to other clients, so observers see the same trail.
 */
@FunctionalInterface
public interface TrailAppearanceOverride {
    /**
     * @param attacker The player performing the swing.
     * @param stack The item stack the swing is performed with.
     * @param resolved The appearance resolved so far (trail config, weapon attributes, earlier overrides).
     * @return The appearance to use, or null to leave `resolved` untouched.
     */
    @Nullable TrailAppearance override(Player attacker, ItemStack stack, TrailAppearance resolved);

    /**
     * Registered overrides, applied in registration order, each receiving the result of the previous one.
     */
    List<TrailAppearanceOverride> REGISTERED = new ArrayList<>();

    static void register(TrailAppearanceOverride override) {
        REGISTERED.add(override);
    }

    @Nullable static TrailAppearance apply(Player attacker, ItemStack stack, @Nullable TrailAppearance resolved) {
        if (resolved == null) {
            return null;
        }
        for (var override: REGISTERED) {
            try {
                var overridden = override.override(attacker, stack, resolved);
                if (overridden != null) {
                    resolved = overridden;
                }
            } catch (Exception e) {
                BetterCombatMod.LOGGER.error("Failed to apply trail appearance override", e);
            }
        }
        return resolved;
    }
}
