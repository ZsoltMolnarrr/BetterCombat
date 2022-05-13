package net.bettercombat.client.collision;

import net.bettercombat.api.AttackStyle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public interface AttackTargetFinder {
    List<Entity> findAttackTargets(PlayerEntity player, float attackRange, AttackStyle attackStyle);
}
