package net.bettercombat.client.collision;

import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributes.Attack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
        Vec3d origin = getInitialTracingPoint(player);
        List<Entity> entities = getInitialTargets(player, cursorTarget, attackRange);
        var obb = new OrientedBoundingBoxFilter(player, origin, attack.direction(), attackRange);
        entities = obb.filter(entities);
        var radial = new RadialFilter(origin, obb.obb.axisZ, attackRange, attack.angle());
        entities = radial.filter(entities);
        return new TargetResult(entities, obb.obb);
    }

    public static List<Entity> findAttackTargets(PlayerEntity player, Entity cursorTarget, Attack attack, double attackRange) {
        return findAttackTargetResult(player, cursorTarget, attack, attackRange).entities;
    }

    public static Vec3d getInitialTracingPoint(PlayerEntity player) {
        double shoulderHeight = player.getHeight() * 0.2 * player.getScaleFactor();
        return player.getEyePos().subtract(0, shoulderHeight, 0);
    }

    public static List<Entity> getInitialTargets(PlayerEntity player, Entity cursorTarget, double attackRange) {
        Box box = player.getBoundingBox().expand(attackRange * 2.0 + 1.0);
        List<Entity> entities = player
                .world
                .getNonSpectatingEntities(LivingEntity.class, box)
                .stream()
                .filter(entity -> entity != player && entity != cursorTarget && entity.isAttackable())
                .collect(Collectors.toList());
        if (cursorTarget != null && cursorTarget.isAttackable()) {
            entities.add(cursorTarget);
        }
        return entities;
    }

    public interface Filter {
        List<Entity> filter(List<Entity> entities);
    }

    public static class OrientedBoundingBoxFilter implements Filter {
        final private PlayerEntity player;
        final private double attackRange;
        public OrientedBoundingBox obb;

        public OrientedBoundingBoxFilter(PlayerEntity player, Vec3d origin, WeaponAttributes.SwingDirection direction, double attackRange) {
            this.player = player;
            this.attackRange = attackRange;
            Vec3d size = WeaponHitBoxes.createHitbox(direction, attackRange);
            obb = new OrientedBoundingBox(origin, size, player.getPitch(), player.getYaw())
                    .offsetAlongAxisZ(size.z / 2F)
                    .updateVertex();
        }

        @Override
        public List<Entity> filter(List<Entity> entities) {
            return entities.stream()
                    .filter(entity -> obb.intersects(entity.getBoundingBox()))
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
            this.attackAngle = MathHelper.clamp(attackAngle, 0, 180);
        }

        @Override
        public List<Entity> filter(List<Entity> entities) {
            return entities.stream()
                    .filter(entity -> {
                        Vec3d distanceVector = CollisionHelper.distanceVector(origin, entity.getBoundingBox());
                        return distanceVector.length() <= attackRange
                                && ((attackAngle == 0) || CollisionHelper.angleBetween(distanceVector, orientation) <= (attackAngle / 2.0));
                    })
                    .collect(Collectors.toList());
        }
    }
}