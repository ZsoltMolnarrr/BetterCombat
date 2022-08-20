package net.bettercombat.client.collision;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class OrientedBoundingBox {

    // TOPOLOGY

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
    public Vec3d axisX;
    public Vec3d axisY;
    public Vec3d axisZ;

    // DERIVED PROPERTIES
    public Vec3d scaledAxisX;
    public Vec3d scaledAxisY;
    public Vec3d scaledAxisZ;
    public Matrix3f rotation = new Matrix3f();
    public Vec3d vertex1;
    public Vec3d vertex2;
    public Vec3d vertex3;
    public Vec3d vertex4;
    public Vec3d vertex5;
    public Vec3d vertex6;
    public Vec3d vertex7;
    public Vec3d vertex8;
    public Vec3d[] vertices;

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

    public OrientedBoundingBox(Box box) {
        this.center = new Vec3d((box.maxX + box.minX) / 2.0, (box.maxY + box.minY) / 2.0, (box.maxZ + box.minZ) / 2.0);
        this.extent = new Vec3d(Math.abs(box.maxX - box.minX) / 2.0, Math.abs(box.maxY - box.minY) / 2.0, Math.abs(box.maxZ - box.minZ) / 2.0);
        this.axisX = new Vec3d(1, 0, 0);
        this.axisY = new Vec3d(0, 1, 0);
        this.axisZ = new Vec3d(0, 0, 1);
    }

    public OrientedBoundingBox(OrientedBoundingBox obb) {
        this.center = obb.center;
        this.extent = obb.extent;
        this.axisX = obb.axisX;
        this.axisY = obb.axisY;
        this.axisZ = obb.axisZ;
    }

    public OrientedBoundingBox copy() {
        return new OrientedBoundingBox(this);
    }

    // 2. CONFIGURE

    public OrientedBoundingBox offsetAlongAxisX(double offset) {
        this.center = this.center.add(axisX.multiply(offset));
        return this;
    }

    public OrientedBoundingBox offsetAlongAxisY(double offset) {
        this.center = this.center.add(axisY.multiply(offset));
        return this;
    }

    public OrientedBoundingBox offsetAlongAxisZ(double offset) {
        this.center = this.center.add(axisZ.multiply(offset));
        return this;
    }

    public OrientedBoundingBox offset(Vec3d offset) {
        this.center = this.center.add(offset);
        return this;
    }

    public OrientedBoundingBox scale(double scale) {
        this.extent = this.extent.multiply(scale);
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

        scaledAxisX = axisX.multiply(extent.x);
        scaledAxisY = axisY.multiply(extent.y);
        scaledAxisZ = axisZ.multiply(extent.z);

        vertex1 = center.subtract(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY);
        vertex2 = center.subtract(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY);
        vertex3 = center.subtract(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);
        vertex4 = center.subtract(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY);
        vertex5 = center.add(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY);
        vertex6 = center.add(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY);
        vertex7 = center.add(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);
        vertex8 = center.add(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY);

        vertices = new Vec3d[]{
                vertex1,
                vertex2,
                vertex3,
                vertex4,
                vertex5,
                vertex6,
                vertex7,
                vertex8
        };

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

    public boolean intersects(Box boundingBox) {
        var otherOBB = new OrientedBoundingBox(boundingBox).updateVertex();
        return Intersects(this, otherOBB);
    }

    public boolean intersects(OrientedBoundingBox otherOBB) {
        return Intersects(this, otherOBB);
    }

    /**
     * Calculates if there is intersection between given OBBs.
     * Separating Axes Theorem implementation.
     */
    public static boolean Intersects(OrientedBoundingBox a, OrientedBoundingBox b)  {
        if (Separated(a.vertices, b.vertices, a.scaledAxisX))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisY))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisZ))
            return false;

        if (Separated(a.vertices, b.vertices, b.scaledAxisX))
            return false;
        if (Separated(a.vertices, b.vertices, b.scaledAxisY))
            return false;
        if (Separated(a.vertices, b.vertices, b.scaledAxisZ))
            return false;

        if (Separated(a.vertices, b.vertices, a.scaledAxisX.crossProduct(b.scaledAxisX)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisX.crossProduct(b.scaledAxisY)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisX.crossProduct(b.scaledAxisZ)))
            return false;

        if (Separated(a.vertices, b.vertices, a.scaledAxisY.crossProduct(b.scaledAxisX)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisY.crossProduct(b.scaledAxisY)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisY.crossProduct(b.scaledAxisZ)))
            return false;

        if (Separated(a.vertices, b.vertices, a.scaledAxisZ.crossProduct(b.scaledAxisX)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisZ.crossProduct(b.scaledAxisY)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisZ.crossProduct(b.scaledAxisZ)))
            return false;

        return true;
    }

    private static boolean Separated(Vec3d[] vertsA, Vec3d[] vertsB, Vec3d axis)  {
        // Handles the crossProduct product = {0,0,0} case
        if (axis.equals(Vec3d.ZERO))
            return false;

        var aMin = Double.POSITIVE_INFINITY;
        var aMax = Double.NEGATIVE_INFINITY;
        var bMin = Double.POSITIVE_INFINITY;
        var bMax = Double.NEGATIVE_INFINITY;

        // Define two intervals, a and b. Calculate their min and max values
        for (var i = 0; i < 8; i++)
        {
            var aDist = vertsA[i].dotProduct(axis);
            aMin = (aDist < aMin) ? aDist : aMin;
            aMax = (aDist > aMax) ? aDist : aMax;
            var bDist = vertsB[i].dotProduct(axis);
            bMin = (bDist < bMin) ? bDist : bMin;
            bMax = (bDist > bMax) ? bDist : bMax;
        }

        // One-dimensional intersection test between a and b
        var longSpan = Math.max(aMax, bMax) - Math.min(aMin, bMin);
        var sumSpan = aMax - aMin + bMax - bMin;
        return longSpan >= sumSpan; // > to treat touching as intersection
    }
}
