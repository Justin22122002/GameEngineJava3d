package org.test.math;

public class Vec3D
{
    public double x, y, z;

    public Vec3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D addVector(Vec3D in, Vec3D in2)
    {
        return new Vec3D(in.x + in2.x, in.y + in2.y, in.z + in2.z);
    }

    public Vec3D subtractVector (Vec3D in, Vec3D in2)
    {
        return new Vec3D(in.x - in2.x, in.y - in2.y, in.z - in2.z);
    }

    public Vec3D multiplyVector(Vec3D in, double f)
    {
        return new Vec3D(in.x * f, in.y * f, in.z * f);
    }

    public Vec3D divideVector(Vec3D in, double f)
    {
        return new Vec3D(in.x / f, in.y / f, in.z / f);
    }

    public double dotProduct(Vec3D in, Vec3D in2)
    {
        return in.x * in2.x + in.y * in2.y + in.z * in2.z;
    }

    public double length(Vec3D in)
    {
        return Math.sqrt(this.dotProduct(in, in));
    }

    public Vec3D normalizeVector(Vec3D in)
    {
        double f = length(in);
        return this.divideVector(in, f);
    }

    public Vec3D crossProduct(Vec3D in, Vec3D in2)
    {
        Vec3D out = new Vec3D(0, 0, 0);

        out.x = in.y * in2.z - in.z * in2.y;
        out.y = in.z * in2.x - in.x * in2.z;
        out.z = in.x * in2.y - in.y * in2.x;

        return out;
    }

    @Override
    public String toString()
    {
        return "Vec3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
