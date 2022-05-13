package net.bettercombat.client.collision;

import net.bettercombat.api.AttackStyle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.stream.Collectors;

public class AxisAlignedTargetFinder implements AttackTargetFinder {
    @Override
    public List<Entity> findAttackTargets(PlayerEntity player, float attackRange, AttackStyle attackStyle) {
        Box box = player.getBoundingBox().expand(attackRange, 0, attackRange);
        List<Entity> targets = player.world.getNonSpectatingEntities(LivingEntity.class, box)
                .stream()
                .filter(entity ->
                    entity.distanceTo(player) <= attackRange
                        && entity != player
                )
                .collect(Collectors.toList());
        return targets;
    }
}
