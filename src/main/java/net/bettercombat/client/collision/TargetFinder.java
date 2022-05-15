package net.bettercombat.client.collision;

import net.bettercombat.api.MeleeWeaponAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
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

    public static TargetResult findAttackTargetResult(PlayerEntity player, MeleeWeaponAttributes attributes) {
        Vec3d origin = getInitialTracingPoint(player);
        List<Entity> entities = getInitialTargets(player, attributes.attackRange);
        var obb = new OrientedBoundingBoxFilter(player, origin, attributes.attackRange);
        entities = obb.filter(entities);
        var radial = new RadialFilter(origin, obb.obb.axisZ, attributes.attackRange, attributes.attackAngle);
        entities = radial.filter(entities);
        return new TargetResult(entities, obb.obb);
    }

    public static List<Entity> findAttackTargets(PlayerEntity player, MeleeWeaponAttributes attributes) {
        return findAttackTargetResult(player, attributes).entities;
    }

    public static Vec3d getInitialTracingPoint(PlayerEntity player) {
        double shoulderHeight = player.getHeight() * 0.2 * player.getScaleFactor();
        return player.getEyePos().subtract(0, shoulderHeight, 0);
    }

    public static List<Entity> getInitialTargets(PlayerEntity player, double attackRange) {
        Box box = player.getBoundingBox().expand(5F); // TODO: Proper expansion
        return player
                .world
                .getNonSpectatingEntities(LivingEntity.class, box)
                .stream()
                .filter(entity -> entity != player)
                .collect(Collectors.toList());
    }

    public interface Filter {
        List<Entity> filter(List<Entity> entities);
    }

    public static class OrientedBoundingBoxFilter implements Filter {
        final private PlayerEntity player;
        final private double attackRange;
        public OrientedBoundingBox obb;

        public OrientedBoundingBoxFilter(PlayerEntity player, Vec3d origin, double attackRange) {
            this.player = player;
            this.attackRange = attackRange;
            Vec3d size = new Vec3d(attackRange * 2, attackRange / 2.0, attackRange); // TODO: Proper OBBs for different attack styles
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
            this.attackAngle = attackAngle; // TODO: Clamp in 0 - 180
        }

        @Override
        public List<Entity> filter(List<Entity> entities) {
            return entities.stream()
                    .filter(entity -> {
                        Vec3d distanceVector = CollisionHelper.distanceVector(origin, entity.getBoundingBox());
                        if (MinecraftClient.getInstance().options.attackKey.isPressed()) {
                            System.out.println("Orientation: " + orientation);
                            System.out.println("Distance vector: " + distanceVector);
                            System.out.println("Diff: " + CollisionHelper.angleBetween(distanceVector, orientation) + " required: " + (attackAngle / 2.0));
                        }
                        return distanceVector.length() <= attackRange
                                && CollisionHelper.angleBetween(distanceVector, orientation) <= (attackAngle / 2.0);
                        // TODO: Handle attack angle 0
                    })
                    .collect(Collectors.toList());
        }
    }
}