package net.bettercombat.client.collision;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.WeaponAttributes.Attack;
import net.bettercombat.compatibility.CompatibilityFlags;
import net.bettercombat.compatibility.PehkuiHelper;
import net.bettercombat.logic.TargetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.stream.Collectors;

public class TargetFinder {
    public static class TargetResult {
        public List<Entity> entities;
        public OrientedBoundingBox obb;
        public TargetResult(List<Entity> entities, OrientedBoundingBox obb) {
            this.entities = entities;
            this.obb = obb;
        }
    }

    public static TargetResult findAttackTargetResult(PlayerEntity player, Entity cursorTarget, Attack attack, double attackRange) {
//        long startTime = System.nanoTime();
        Vec3d origin = getInitialTracingPoint(player);
        List<Entity> entities = getInitialTargets(player, cursorTarget, attackRange);

        if (CompatibilityFlags.usePehkui) {
            attackRange = attackRange * PehkuiHelper.getScale(player);
        }

        boolean isSpinAttack = attack.angle() > 180;
        Vec3d size = WeaponHitBoxes.createHitbox(attack.hitbox(), attackRange, isSpinAttack);
        var obb = new OrientedBoundingBox(origin, size, player.getPitch(), player.getYaw());
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
        return new TargetResult(entities, obb);
    }

    public static List<Entity> findAttackTargets(PlayerEntity player, Entity cursorTarget, Attack attack, double attackRange) {
        return findAttackTargetResult(player, cursorTarget, attack, attackRange).entities;
    }

    public static Vec3d getInitialTracingPoint(PlayerEntity player) {
        double shoulderHeight = player.getHeight() * 0.15 * player.getScaleFactor();
        return player.getEyePos().subtract(0, shoulderHeight, 0);
    }

    public static List<Entity> getInitialTargets(PlayerEntity player, Entity cursorTarget, double attackRange) {
        Box box = player.getBoundingBox().expand(attackRange * BetterCombat.config.target_search_range_multiplier + 1.0);
        List<Entity> entities = player
                .world
                .getOtherEntities(player, box, entity ->  !entity.isSpectator() && entity.canHit())
                .stream()
                .filter(entity -> {
                    var result = entity != player
                            && entity != cursorTarget
                            && entity.isAttackable()
                            && (BetterCombat.config.allow_attacking_mount || !entity.equals(player.getVehicle()))
                            && TargetHelper.getRelation(player, entity) == TargetHelper.Relation.HOSTILE;
                    return result;
                })
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
                    .filter(entity -> obb.intersects(entity.getBoundingBox())
                                || obb.contains(entity.getPos().add(0, entity.getHeight() / 2F, 0))
                    )
                    .collect(Collectors.toList());
        }
    }

    public static class RadialFilter implements Filter {
        final private Vec3d origin;
        final private Vec3d orientation;
        final private double attackRange;
        final private double attackAngle;

        public RadialFilter(Vec3d origin, Vec3d orientation, double attackRange, double attackAngle) {
            this.origin = origin;
            this.orientation = orientation;
            this.attackRange = attackRange;
            this.attackAngle = MathHelper.clamp(attackAngle, 0, 360);
        }

        @Override
        public List<Entity> filter(List<Entity> entities) {
            return entities.stream()
                    .filter(entity -> {
                        var maxAngleDif = (attackAngle / 2.0);
                        Vec3d distanceVector = CollisionHelper.distanceVector(origin, entity.getBoundingBox());
                        Vec3d positionVector = entity.getPos().add(0, entity.getHeight() / 2F, 0).subtract(origin);
                        return distanceVector.length() <= attackRange
                                && ((attackAngle == 0)
                                    || (CollisionHelper.angleBetween(positionVector, orientation) <= maxAngleDif
                                    || CollisionHelper.angleBetween(distanceVector, orientation) <= maxAngleDif))
                                && (BetterCombat.config.allow_attacking_thru_walls
                                    || rayContainsNoObstacle(origin, origin.add(distanceVector))
                                    || rayContainsNoObstacle(origin, origin.add(positionVector)));
                    })
                    .collect(Collectors.toList());
        }

        private static boolean rayContainsNoObstacle(Vec3d start, Vec3d end) {
            var client = MinecraftClient.getInstance();
            var world = client.world;
            var hit = client.world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, client.player));
            return hit.getType() != HitResult.Type.BLOCK;
        }
    }
}