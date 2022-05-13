package net.bettercombat.client.collision;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class OrientedBoundingBox {

    // TOPOLOGY
    //
    // Y ^       8   +-------+   7     axisY   axisZ
    //   |          /|      /|             | /
    //   |     4   +-------+ | 3           |/
    //   |  Z      | |     | |             +-- axisX
    //   |   /   5 | +-----|-+  6       Center
    //   |  /      |/      |/
    //   | /   1   +-------+   2
    //   |/
    //   +--------------------> X

    // DEFINITIVE PROPERTIES

    // Center position of the cuboid
    public Vec3d center;

    // Extent defines the half size in all directions
    public Vec3d extent;

    // Orthogonal basis vectors define orientation
    public Vec3d axisZ;
    public Vec3d axisY;
    public Vec3d axisX;

    // DERIVED PROPERTIES
    public Vec3d orientationX;
    public Vec3d orientationY;
    public Vec3d orientationZ;
    public Matrix3f rotation = new Matrix3f();
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
        this.axisZ = Vec3d.fromPolar(yaw, pitch).normalize();
        this.axisY = Vec3d.fromPolar(yaw + 90, pitch).negate().normalize();
        this.axisX = axisZ.crossProduct(axisY);
    }

    public OrientedBoundingBox(Vec3d center, Vec3d size, float yaw, float pitch) {
        this(center,size.x, size.y, size.z, yaw, pitch);
    }

    // 2. CONFIGURE

    public OrientedBoundingBox offsetU(double offset) {
        this.center = this.center.add(axisZ.multiply(offset));
        return this;
    }

    public OrientedBoundingBox offset(Vec3d offset) {
        this.center = this.center.add(offset);
        return this;
    }

    // 3. UPDATE

    public OrientedBoundingBox updateVertex() {
        rotation.set(0,0, (float) axisX.x);
        rotation.set(0,1, (float) axisX.y);
        rotation.set(0,2, (float) axisX.z);
        rotation.set(1,0, (float) axisY.x);
        rotation.set(1,1, (float) axisY.y);
        rotation.set(1,2, (float) axisY.z);
        rotation.set(2,0, (float) axisZ.x);
        rotation.set(2,1, (float) axisZ.y);
        rotation.set(2,2, (float) axisZ.z);

        orientationX = axisX.multiply(extent.x);
        orientationY = axisY.multiply(extent.y);
        orientationZ = axisZ.multiply(extent.z);

        vertex1 = center.subtract(orientationZ).subtract(orientationX).subtract(orientationY);
        vertex2 = center.subtract(orientationZ).add(orientationX).subtract(orientationY);
        vertex3 = center.subtract(orientationZ).add(orientationX).add(orientationY);
        vertex4 = center.subtract(orientationZ).subtract(orientationX).add(orientationY);
        vertex5 = center.add(orientationZ).subtract(orientationX).subtract(orientationY);
        vertex6 = center.add(orientationZ).add(orientationX).subtract(orientationY);
        vertex7 = center.add(orientationZ).add(orientationX).add(orientationY);
        vertex8 = center.add(orientationZ).subtract(orientationX).add(orientationY);

        return this;
    }

    // 4. CHECK INTERSECTIONS

    public boolean contains(Vec3d point) {
        Vec3f distance = new Vec3f(point.subtract(center));
        distance.transform(rotation);
        return Math.abs(distance.getX()) < extent.x &&
                Math.abs(distance.getY()) < extent.y &&
                Math.abs(distance.getZ()) < extent.z;
    }

    // Calculates intersection with
    public boolean intersectsBoundingBox(Box boundingBox) {
        // TODO
        return false;
    }

    // MISC / HELPERS
}
