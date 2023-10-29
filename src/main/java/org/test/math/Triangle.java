package org.test.math;

public class Triangle
{
    public Vec3D vec3D, vec3D2, vec3D3;

    public Triangle(Vec3D vec3D, Vec3D vec3D2, Vec3D vec3D3)
    {
        this.vec3D = vec3D;
        this.vec3D2 = vec3D2;
        this.vec3D3 = vec3D3;
    }

    @Override
    public String toString()
    {
        return "Triangle{" +
                "vec3D=" + vec3D +
                ", vec3D2=" + vec3D2 +
                ", vec3D3=" + vec3D3 +
                '}';
    }
}
