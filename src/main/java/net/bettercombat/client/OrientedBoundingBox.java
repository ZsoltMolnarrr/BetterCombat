package net.bettercombat.client;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class OrientedBoundingBox {

    // Helpers
    private static final Vec3d AXIS_X = new Vec3d(1.0f, 0.0f, 0.0f);
    private static final Vec3d AXIS_Y = new Vec3d(0.0f, 1.0f, 0.0f);
    private static final Vec3d AXIS_Z = new Vec3d(0.0f, 0.0f, 1.0f);

    // Center position of the cuboid
    public Vec3d center;
    // Extent vector of the cuboid


    //    ORIENTATION REPRESENTATION
    //
    // Y ^       8   +-------+   7
    //   |          /|      /|
    //   |     4   +-------+ | 3
    //   |  Z      | |     | |
    //   |   /   5 | +-----|-+  6
    //   |  /      |/      |/
    //   | /   1   +-------+   2
    //   |/
    //   +--------------------> X


    public Vec3d orientation_u;
    public Vec3d orientation_v;
    public Vec3d orientation_w;

    public Vec3d extent;
    public Vec3d vertex1;
    public Vec3d vertex2;
    public Vec3d vertex3;
    public Vec3d vertex4;
    public Vec3d vertex5;
    public Vec3d vertex6;
    public Vec3d vertex7;
    public Vec3d vertex8;

    //    VERTEX REPRESENTATION
    //
    // Y ^       8   +-------+   7
    //   |          /|      /|
    //   |     4   +-------+ | 3
    //   |  Z      | |     | |
    //   |   /   5 | +-----|-+  6
    //   |  /      |/      |/
    //   | /   1   +-------+   2
    //   |/
    //   +--------------------> X

    private void updateVertex() {
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
    }

    // Convenience constructor
    public OrientedBoundingBox(Vec3d center, double width, double height, double depth, float yaw, float pitch) {
        this.center = center;
        this.extent = new Vec3d(width/2.0, height/2.0, depth/2.0);
        this.orientation_u = Vec3d.fromPolar(new Vec2f(yaw, pitch))
                .normalize();
        this.orientation_w = Vec3d.fromPolar(new Vec2f (yaw + 90, pitch))
                .normalize();
        this.orientation_v = orientation_u.crossProduct(orientation_w)
                .normalize();
        this.updateVertex();
    }

    // Calculates intersection with
    public boolean intersectsBoundingBox(Box boundingBox) {
        // TODO
        return false;
    }

    public void printDebug() {
        Vec3d extent_x = orientation_w.multiply(extent.x);
        Vec3d extent_y = orientation_v.multiply(extent.y);
        Vec3d extent_z = orientation_u.multiply(extent.z);
        System.out.println("Center: " + vec3Short(center) + "orientation: " + vec3Short(orientation_u) + " Extent: " + vec3Short(extent) );
        System.out.println("extent_x: " + vec3Short(extent_x)
                + "extent_y: " + vec3Short(extent_y)
                + "extent_z: " + vec3Short(extent_z));
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

    public String toString() {
        return "1:" + vertex1
                + " 2:" + vertex2
                + " 3:" + vertex3
                + " 4:" + vertex4
                + " 5:" + vertex5
                + " 6:" + vertex6
                + " 7:" + vertex7
                + " 8:" + vertex8;
    }
}
