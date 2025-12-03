package net.bettercombat.api.fx;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Registry for item stack predicates used for conditional weapon trail appearances.
 */
public class ItemConditions {
    private static final Map<String, Predicate<ItemStack>> CONDITIONS = new HashMap<>();

    static {
        // Register built-in conditions
        register("is_enchanted", ItemStack::hasEnchantments);
    }

    /**
     * Registers a new condition predicate.
     * @param id The identifier for the condition
     * @param predicate The predicate to test ItemStacks
     */
    public static void register(String id, Predicate<ItemStack> predicate) {
        CONDITIONS.put(id, predicate);
    }

    /**
     * Gets a condition predicate by its identifier.
     * @param id The condition identifier
     * @return The predicate, or null if not found
     */
    public static Predicate<ItemStack> get(String id) {
        return CONDITIONS.get(id);
    }

    /**
     * Tests an ItemStack against a condition.
     * @param id The condition identifier
     * @param itemStack The ItemStack to test
     * @return true if the condition is met, false otherwise (including if condition doesn't exist)
     */
    public static boolean test(String id, ItemStack itemStack) {
        Predicate<ItemStack> predicate = CONDITIONS.get(id);
        if (predicate == null) {
            return false;
        }
        return predicate.test(itemStack);
    }

    /**
     * Checks if a condition is registered.
     * @param id The condition identifier
     * @return true if the condition exists
     */
    public static boolean has(String id) {
        return CONDITIONS.containsKey(id);
    }
}
