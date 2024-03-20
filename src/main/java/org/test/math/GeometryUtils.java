package org.test.math;

/**
 * A utility class for performing geometric calculations.
 */
public class GeometryUtils
{
    /**
     * Calculates the intersection point of a plane with a line segment defined by its start and end points.
     *
     * @param plane_p   A point on the plane.
     * @param plane_n   The normal vector of the plane.
     * @param lineStart The start point of the line segment.
     * @param lineEnd   The end point of the line segment.
     * @param tt         A Double object to store the intersection parameter (t).
     * @return The intersection point of the plane with the line segment.
     */
    public static Vec3D vectorIntersectPlane(Vec3D plane_p, Vec3D plane_n, Vec3D lineStart, Vec3D lineEnd, ExtraData tt)
    {
        plane_n = plane_n.normalizeVector();
        double plane_d = - plane_n.dotProduct(plane_p);
        double ad = lineStart.dotProduct(plane_n);
        double bd = lineEnd.dotProduct(plane_n);
        double t = (-plane_d - ad) / (bd - ad);
        tt.t = t;
        Vec3D lineStartToEnd = lineEnd.subtractVector(lineStart);
        Vec3D lineToIntersect = lineStartToEnd.multiplyVector(t);
        return lineStart.addVector(lineToIntersect);
    }
}
