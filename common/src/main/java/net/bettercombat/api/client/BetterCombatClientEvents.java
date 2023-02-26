package net.bettercombat.api.client;

import net.bettercombat.api.AttackHand;
import net.bettercombat.api.event.Publisher;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BetterCombatClientEvents {

    /**
     * Called when player starts attack upswing (aka windup).
     */
    public static final Publisher<PlayerAttackStart> ATTACK_START = new Publisher<>();

    @FunctionalInterface
    public interface PlayerAttackStart {
        void onPlayerAttackStart(ClientPlayerEntity player, AttackHand attackHand);
    }

    /**
     * Called when player hits some targets (can be zero or more targets).
     */
    public static final Publisher<PlayerAttackHit> ATTACK_HIT = new Publisher<>();

    @FunctionalInterface
    public interface PlayerAttackHit {
        void onPlayerAttackStart(ClientPlayerEntity player, AttackHand attackHand, List<Entity> targets, @Nullable Entity cursorTarget);
    }
}
