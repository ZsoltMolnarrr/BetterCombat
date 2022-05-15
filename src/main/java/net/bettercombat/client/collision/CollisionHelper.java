package net.bettercombat.client.collision;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class CollisionHelper {
    public static double angleBetween(Vec3d a, Vec3d b) {
        var cosineTheta = a.dotProduct(b) / (a.length() * b.length());
        return Math.acos(cosineTheta) * (180.0 / Math.PI);
    }

    /**
     * Calculates distance vector FROM the given point TO the given box.
     */
    public static Vec3d distanceVector(Vec3d point, Box box) {
//        var dx = Math.max(Math.max(box.minX - point.x, box.maxX - point.x), 0);
//        var dy = Math.max(Math.max(box.minY - point.y, box.maxY - point.y), 0);
//        var dz = Math.max(Math.max(box.minZ - point.z, box.maxZ - point.z), 0);
        double dx = 0;
        if (box.minX > point.x) {
            dx = box.minX - point.x;
        } else if (box.maxX < point.x) {
            dx = box.maxX - point.x;
        }
        double dy = 0;
        if (box.minY > point.y) {
            dy = box.minY - point.y;
        } else if (box.maxY < point.y) {
            dy = box.maxY - point.y;
        }
        double dz = 0;
        if (box.minZ > point.z) {
            dz = box.minZ - point.z;
        } else if (box.maxZ < point.z) {
            dz = box.maxZ - point.z;
        }
        return new Vec3d(dx, dy, dz);
    }

    public static double distance(Vec3d point, Box box) {
        return distanceVector(point, box).length();
    }
}
