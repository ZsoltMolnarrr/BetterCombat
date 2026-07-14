package net.bettercombat.client.collision;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.WeaponAttributes.Attack;
import net.bettercombat.api.client.AttackRangeExtensions;
import net.bettercombat.logic.TargetHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TargetFinder {
    public static class TargetResult {
        public Entity cursorTarget;
        public List<Entity> entities;
        public OrientedBoundingBox obb;
        public TargetResult(Entity cursorTarget, List<Entity> entities, OrientedBoundingBox obb) {
            this.entities = entities;
            this.obb = obb;
        }
    }

    public static TargetResult findAttackTargetResult(Player player, Entity cursorTarget, Attack attack, double attackRange) {
//        long startTime = System.nanoTime();
        Vec3 origin = getInitialTracingPoint(player);
        List<Entity> entities = getInitialTargets(player, cursorTarget, attackRange);

        if (!AttackRangeExtensions.sources().isEmpty()) {
            attackRange = applyAttackRangeModifiers(player, attackRange);
        }

        boolean isSpinAttack = attack.angle() > 180;
        Vec3 size = WeaponHitBoxes.createHitbox(attack.hitbox(), attackRange, isSpinAttack);
        var obb = new OrientedBoundingBox(origin, size, player.getXRot(), player.getYRot());
        if (!isSpinAttack) {
            obb = obb.offsetAlongAxisZ(size.z / 2F);
        }
        obb.updateVertex();

        var collisionFilter = new CollisionFilter(obb);
        entities = collisionFilter.filter(entities);
        var radialFilter = new RadialFilter(origin, obb.axisZ, attackRange, attack.angle());
        entities = radialFilter.filter(entities);
//        long elapsedTime = System.nanoTime() - startTime;
//        System.out.println("TargetResult findAttackTargetResult (ms): " + ((double)elapsedTime) / 1000000.0);
        return new TargetResult(cursorTarget, entities, obb);
    }

    private static double applyAttackRangeModifiers(Player player, double attackRange) {
        var context = new AttackRangeExtensions.Context(player,attackRange);
        var modifiers = AttackRangeExtensions.sources()
                .stream()
                .map(function -> function.apply(context))
                .sorted(Comparator.comparingInt(AttackRangeExtensions.Modifier::operationOrder))
                .toList();
        var result = attackRange;
        for (var modifier: modifiers) {
            switch (modifier.operation()) {
                case ADD -> {
                    result += modifier.value();
                }
                case MULTIPLY -> {
                    result *= modifier.value();
                }
            }
        }
        return result;
    }

    public static List<Entity> findAttackTargets(Player player, Entity cursorTarget, Attack attack, double attackRange) {
        return findAttackTargetResult(player, cursorTarget, attack, attackRange).entities;
    }

    public static Vec3 getInitialTracingPoint(Player player) {
        double shoulderHeight = player.getBbHeight() * 0.15 * player.getAgeScale();
        return player.getEyePosition().subtract(0, shoulderHeight, 0);
    }

    public static List<Entity> getInitialTargets(Player player, Entity cursorTarget, double attackRange) {
        AABB box = player.getBoundingBox().inflate(attackRange * BetterCombatMod.config.target_search_range_multiplier + 1.0);
        List<Entity> entities = player
                .level()
                .getEntities(player, box, entity ->  !entity.isSpectator() && entity.isPickable())
                .stream()
                .filter(entity -> entity != player
                        && entity.isAttackable()
                        && entity != cursorTarget
                        && TargetHelper.isHitAllowed(false, TargetHelper.getRelation(player, entity)) // isDirect: false due to not being the cursor target
                        && (!entity.equals(player.getVehicle()) || TargetHelper.isAttackableMount(entity)))
                .collect(Collectors.toList());
        if (cursorTarget != null && cursorTarget.isAttackable()) {
            entities.add(cursorTarget);
        }
        return entities;
    }


    public interface Filter {
        List<Entity> filter(List<Entity> entities);
    }

    public static class CollisionFilter implements Filter {
        private OrientedBoundingBox obb;

        public CollisionFilter(OrientedBoundingBox obb) {
            this.obb = obb;
        }

        @Override
        public List<Entity> filter(List<Entity> entities) {
            return entities.stream()
                    .filter(entity -> obb.intersects(entity.getBoundingBox().inflate(entity.getPickRadius()))
                                || obb.contains(entity.position().add(0, entity.getBbHeight() / 2F, 0))
                    )
                    .collect(Collectors.toList());
        }
    }

    public static class RadialFilter implements Filter {
        final private Vec3 origin;
        final private Vec3 orientation;
        final private double attackRange;
        final private double attackAngle;

        public RadialFilter(Vec3 origin, Vec3 orientation, double attackRange, double attackAngle) {
            this.origin = origin;
            this.orientation = orientation;
            this.attackRange = attackRange;
            this.attackAngle = Mth.clamp(attackAngle, 0, 360);
        }

        @Override
        public List<Entity> filter(List<Entity> entities) {
            return entities.stream()
                    .filter(entity -> {
                        var maxAngleDif = (attackAngle / 2.0);
                        Vec3 distanceVector = CollisionHelper.distanceVector(origin, entity.getBoundingBox());
                        Vec3 positionVector = entity.position().add(0, entity.getBbHeight() / 2F, 0).subtract(origin);
                        return distanceVector.length() <= attackRange
                                && ((attackAngle == 0)
                                    || (CollisionHelper.angleBetween(positionVector, orientation) <= maxAngleDif
                                    || CollisionHelper.angleBetween(distanceVector, orientation) <= maxAngleDif))
                                && (BetterCombatMod.config.allow_attacking_thru_walls
                                    || rayContainsNoObstacle(origin, origin.add(distanceVector))
                                    || rayContainsNoObstacle(origin, origin.add(positionVector)));
                    })
                    .collect(Collectors.toList());
        }

        private static boolean rayContainsNoObstacle(Vec3 start, Vec3 end) {
            var client = Minecraft.getInstance();
            BlockHitResult hit = null;
            if (client.level != null) {
                hit = client.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, client.player));
            }
            if (hit != null) {
                return hit.getType() != HitResult.Type.BLOCK;
            }
            return false;
        }
    }
}