package net.bettercombat.client.collision;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BoxHelper {
    public static double distance(Box box, Vec3d point) {
        var dx = Math.max(Math.max(box.minX - point.x, point.x - box.maxX), 0);
        var dy = Math.max(Math.max(box.minY - point.y, point.y - box.maxY), 0);
        var dz = Math.max(Math.max(box.minZ - point.z, point.z - box.maxZ), 0);
        return new Vec3d(dx, dy, dz).length();
    }
}
