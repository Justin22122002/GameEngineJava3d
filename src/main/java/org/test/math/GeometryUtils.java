package org.test.math;

import org.test.math.vector.Vector3D;

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
    public static Vector3D vectorIntersectPlane(Vector3D plane_p, Vector3D plane_n, Vector3D lineStart, Vector3D lineEnd, ExtraData tt)
    {
        plane_n = plane_n.normalizeVector();
        double plane_d = - plane_n.dotProduct(plane_p);
        double ad = lineStart.dotProduct(plane_n);
        double bd = lineEnd.dotProduct(plane_n);
        double t = (-plane_d - ad) / (bd - ad);
        tt.t = t;
        Vector3D lineStartToEnd = lineEnd.subtractVector(lineStart);
        Vector3D lineToIntersect = lineStartToEnd.multiplyVector(t);
        return lineStart.addVector(lineToIntersect);
    }

}
