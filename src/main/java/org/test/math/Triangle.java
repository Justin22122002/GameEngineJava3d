package org.test.math;

import java.awt.*;

public class Triangle
{
    public Vec3D vec3D, vec3D2, vec3D3;
    public Vec2D vec2D, vec2D2, vec2D3;
    public Color color;

    public Triangle(Vec3D vec3D, Vec3D vec3D2, Vec3D vec3D3)
    {
        this.vec3D = vec3D;
        this.vec3D2 = vec3D2;
        this.vec3D3 = vec3D3;
    }

    public Triangle(Vec3D vec3D, Vec3D vec3D2, Vec3D vec3D3, Vec2D vec2D, Vec2D vec2D2, Vec2D vec2D3)
    {
        this.vec3D = vec3D;
        this.vec3D2 = vec3D2;
        this.vec3D3 = vec3D3;
        this.vec2D = vec2D;
        this.vec2D2 = vec2D2;
        this.vec2D3 = vec2D3;
    }

    public Color color(int r, int g, int b)
    {
        this.color = new Color(r, g, b);

        return this.color;
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
