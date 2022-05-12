package net.bettercombat.client;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class OrientedBoundingBox {

    //               TOPOLOGY
    //
    // Y ^       8   +-------+   7         v  u
    //   |          /|      /|             | /
    //   |     4   +-------+ | 3           |/
    //   |  Z      | |     | |             +-- w
    //   |   /   5 | +-----|-+  6       Center
    //   |  /      |/      |/
    //   | /   1   +-------+   2
    //   |/
    //   +--------------------> X

    // Center position of the cuboid
    public Vec3d center;

    // Extent defines the half size in all directions
    public Vec3d extent;

    // Orthogonal basis vectors define orientation
    public Vec3d orientation_u;
    public Vec3d orientation_v;
    public Vec3d orientation_w;

    // Vertices are calculated based on fields from above

    public Vec3d vertex1;
    public Vec3d vertex2;
    public Vec3d vertex3;
    public Vec3d vertex4;
    public Vec3d vertex5;
    public Vec3d vertex6;
    public Vec3d vertex7;
    public Vec3d vertex8;

    // 1. CONSTRUCT

    public OrientedBoundingBox(Vec3d center, double width, double height, double depth, float yaw, float pitch) {
        this.center = center;
        this.extent = new Vec3d(width/2.0, height/2.0, depth/2.0);
        this.orientation_u = Vec3d.fromPolar(yaw, pitch).normalize();
        this.orientation_v = Vec3d.fromPolar(yaw + 90, pitch).negate().normalize();
        this.orientation_w = orientation_u.crossProduct(orientation_v);
    }

    public OrientedBoundingBox(Vec3d center, Vec3d size, float yaw, float pitch) {
        this(center,size.x, size.y, size.z, yaw, pitch);
    }

    // 2. CONFIGURE

    public OrientedBoundingBox offsetU(double offset) {
        this.center = this.center.add(orientation_u.multiply(offset));
        return this;
    }

    // 3. UPDATE

    public OrientedBoundingBox updateVertex() {
        Vec3d extent_x = orientation_w.multiply(extent.x);
        Vec3d extent_y = orientation_v.multiply(extent.y);
        Vec3d extent_z = orientation_u.multiply(extent.z);
        vertex1 = center.subtract(extent_z).subtract(extent_x).subtract(extent_y);
        vertex2 = center.subtract(extent_z).add(extent_x).subtract(extent_y);
        vertex3 = center.subtract(extent_z).add(extent_x).add(extent_y);
        vertex4 = center.subtract(extent_z).subtract(extent_x).add(extent_y);
        vertex5 = center.add(extent_z).subtract(extent_x).subtract(extent_y);
        vertex6 = center.add(extent_z).add(extent_x).subtract(extent_y);
        vertex7 = center.add(extent_z).add(extent_x).add(extent_y);
        vertex8 = center.add(extent_z).subtract(extent_x).add(extent_y);
        return this;
    }

    // 4. CHECK INTERSECTIONS

    // Calculates intersection with
    public boolean intersectsBoundingBox(Box boundingBox) {
        // TODO
        return false;
    }

    // HELPERS

    public void printDebug() {
        Vec3d extent_x = orientation_w.multiply(extent.x);
        Vec3d extent_y = orientation_v.multiply(extent.y);
        Vec3d extent_z = orientation_u.multiply(extent.z);
        System.out.println("Center: " + vec3Short(center) + "orientation: " + vec3Short(orientation_u) + " Extent: " + vec3Short(extent) );
        System.out.println("orientation_u: " + vec3Short(orientation_u)
                + "orientation_v: " + vec3Short(orientation_v)
                + "orientation_w: " + vec3Short(orientation_w));
        System.out.println("1:" + vec3Short(vertex1)
                + " 2:" + vec3Short(vertex2)
                + " 3:" + vec3Short(vertex3)
                + " 4:" + vec3Short(vertex4));
        System.out.println("5:" + vec3Short(vertex5)
                + " 6:" + vec3Short(vertex6)
                + " 7:" + vec3Short(vertex7)
                + " 8:" + vec3Short(vertex8));
    }

    private String vec3Short(Vec3d vec) {
        return "{" + String.format("%.2f", vec.x) + ", "  + String.format("%.2f", vec.y) + ", "  + String.format("%.2f", vec.z) + "}";
    }
}
