package org.test.math;

import java.util.Objects;

import static org.test.math.GeometryUtils.vectorIntersectPlane;

/**
 * Represents a three-dimensional vector in space.
 */
public class Vec3D implements Cloneable
{
    public double x, y, z, w;

    /**
     * Constructs a new Vector3D with the specified coordinates.
     *
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     * @param z The z-coordinate of the vector.
     */
    public Vec3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    /**
     * Constructs a new Vector3D with default coordinates (0, 0, 0).
     */
    public Vec3D()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    /**
     * Sets the parameter of the input vector
     * @param in Input vector
     */
    public void setVector(Vec3D in)
    {
        this.x = in.x;
        this.y = in.y;
        this.z = in.z;
        this.w = in.w;
    }

    /**
     * Adds another vector to this vector and returns the result as a new Vector3D.
     *
     * @param in The vector to add.
     * @return The result of adding the vectors.
     */
    public Vec3D addVector(Vec3D in)
    {
        return new Vec3D(in.x + this.x, in.y + this.y, in.z + this.z);
    }

    /**
     * Subtracts another vector from this vector and returns the result as a new Vector3D.
     *
     * @param in The vector to subtract.
     * @return The result of subtracting the vectors.
     */
    public Vec3D subtractVector(Vec3D in)
    {
        return new Vec3D(this.x - in.x, this.y - in.y, this.z - in.z);
    }

    /**
     * Multiplies this vector by a scalar value and returns the result as a new Vector3D.
     *
     * @param f The scalar value to multiply by.
     * @return The result of the multiplication.
     */
    public Vec3D multiplyVector(double f)
    {
        return new Vec3D(this.x * f, this.y * f, this.z * f);
    }

    /**
     * Divides this vector by a scalar value and returns the result as a new Vector3D.
     *
     * @param f The scalar value to divide by.
     * @return The result of the division.
     * @throws IllegalArgumentException if dividing by zero.
     */
    public Vec3D divideVector(double f)
    {
        return new Vec3D(this.x / f, this.y / f, this.z / f);
    }

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param in The other vector.
     * @return The dot product of the two vectors.
     */
    public double dotProduct(Vec3D in)
    {
        return this.x * in.x + this.y * in.y + this.z * in.z;
    }

    /**
     * Calculates the length (magnitude) of this vector.
     *
     * @return The length of the vector.
     */
    public double length()
    {
        return Math.sqrt(this.dotProduct(this));
    }

    /**
     * Normalizes this vector (i.e., scales it to have unit length) and returns the result as a new Vector3D.
     *
     * @return The normalized vector.
     */
    public Vec3D normalizeVector()
    {
        double f = this.length();
        return this.divideVector(f);
    }

    /**
     * Calculates the cross product of this vector with another vector.
     *
     * @param in The other vector.
     * @return The cross product of the two vectors.
     */
    public Vec3D crossProduct(Vec3D in)
    {
        Vec3D out = new Vec3D(0, 0, 0);

        out.x = this.y * in.z - this.z * in.y;
        out.y = this.z * in.x - this.x * in.z;
        out.z = this.x * in.y - this.y * in.x;

        return out;
    }

    /**
     * Rotates this vector around the x-axis by the specified angle (in radians) and returns the result as a new Vector3D.
     *
     * @param angle The angle of rotation (in radians).
     * @return The rotated vector.
     */
    public Vec3D rotateX(double angle)
    {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vec3D(x, cosA * y - sinA * z, sinA * y + cosA * z);
    }

    /**
     * Rotates this vector around the y-axis by the specified angle (in radians) and returns the result as a new Vector3D.
     *
     * @param angle The angle of rotation (in radians).
     * @return The rotated vector.
     */
    public Vec3D rotateY(double angle)
    {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vec3D(cosA * x + sinA * z, y, -sinA * x + cosA * z);
    }

    /**
     * Calculates the distance from this vector to a plane defined by a normal vector and a point on the plane.
     *
     * @param plane_n The normal vector of the plane.
     * @param plane_p A point on the plane.
     * @return The distance from this vector to the plane.
     */
    public double dist(Vec3D plane_n, Vec3D plane_p)
    {
        return (plane_n.x * this.x + plane_n.y * this.y + plane_n.z * this.z - plane_n.dotProduct(plane_p));
    }

    /**
     * Returns a string representation of this vector.
     *
     * @return A string representation of the vector.
     */
    @Override
    public String toString()
    {
        return "Vector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public Vec3D clone()
    {
        try
        {
            Vec3D clone = (Vec3D) super.clone();
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3D vector3D = (Vec3D) o;
        return Double.compare(x, vector3D.x) == 0
                && Double.compare(y, vector3D.y) == 0
                && Double.compare(z, vector3D.z) == 0
                && Double.compare(w, vector3D.w) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y, z, w);
    }
}
