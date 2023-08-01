package net.bettercombat.api.client;

import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AttackRangeExtensions {
    /**
     * @param player - Player who is about to attack (always the local player)
     * @param attackRange - Non-cumulated, absolute attack range of the attack move
     */
    public record Context(PlayerEntity player, double attackRange) { }


    /**
     * Represents the result of how the attack range should be modified
     * @param value - the amount of attack range to be applied
     * @param operation - the way how to be applied
     */
    public record Modifier(double value, Operation operation) {
        public int operationOrder() { return operation.order; }
    }
    public enum Operation { ADD(0), MULTIPLY(1);
        public final int order;
        Operation(int order) { this.order = order; }
        public int getOrder() { return order; }
    }

    private static final ArrayList<Function<Context, Modifier>> sources = new ArrayList<Function<Context, Modifier>>();

    /**
     * Add a custom function to modify the attack range in an arbitrary way.
     * Can be called at any point, does not depend on any state of the game.
     * Make sure your functions are registered only once.
     * @param source - Your function to modify the range
     */
    public static void register(Function<Context, Modifier> source) {
        sources.add(source);
    }

    public static List<Function<Context, Modifier>> sources() {
        return sources;
    }
}
