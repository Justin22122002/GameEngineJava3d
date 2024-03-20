package org.test.math;

import org.test.gfx.Texture;

import java.awt.*;

import static org.test.math.GeometryUtils.vectorIntersectPlane;

/**
 * Represents a triangle in 3D space.
 */
public class Triangle
{
    // Point Coordinates
    public Vec3D vec3D, vec3D2, vec3D3;
    //Texture Coordinates
    public Vec2D vec2D, vec2D2, vec2D3;
    public Color color;
    public double dp;
    public Texture tex;

    /**
     * Constructs a triangle with three 3D points.
     *
     * @param vec3D  First 3D point of the triangle.
     * @param vec3D2 Second 3D point of the triangle.
     * @param vec3D3 Third 3D point of the triangle.
     */
    public Triangle(Vec3D vec3D, Vec3D vec3D2, Vec3D vec3D3)
    {
        this.vec3D = vec3D;
        this.vec3D2 = vec3D2;
        this.vec3D3 = vec3D3;
    }

    /**
     * Constructs a triangle with three 3D points and their corresponding 2D texture coordinates.
     *
     * @param vec3D  First 3D point of the triangle.
     * @param vec3D2 Second 3D point of the triangle.
     * @param vec3D3 Third 3D point of the triangle.
     * @param vec2D  Texture coordinate corresponding to the first 3D point.
     * @param vec2D2 Texture coordinate corresponding to the second 3D point.
     * @param vec2D3 Texture coordinate corresponding to the third 3D point.
     */
    public Triangle(Vec3D vec3D, Vec3D vec3D2, Vec3D vec3D3, Vec2D vec2D, Vec2D vec2D2, Vec2D vec2D3)
    {
        this.vec3D = vec3D;
        this.vec3D2 = vec3D2;
        this.vec3D3 = vec3D3;
        this.vec2D = vec2D;
        this.vec2D2 = vec2D2;
        this.vec2D3 = vec2D3;
    }

    /**
     * Constructs an empty triangle.
     */
    public Triangle()
    {
        this.vec3D = new Vec3D();
        this.vec3D2 = new Vec3D();
        this.vec3D3 = new Vec3D();
        this.vec2D = new Vec2D(0,0);
        this.vec2D2 = new Vec2D(0,0);
        this.vec2D3 = new Vec2D(0,0);
    }

    /**
     * Sets the color of the triangle.
     *
     * @param r Red component (0-255).
     * @param g Green component (0-255).
     * @param b Blue component (0-255).
     * @return The color set for the triangle.
     */
    public Color color(int r, int g, int b)
    {
        this.color = new Color(r, g, b);

        return this.color;
    }

    /**
     * Clips the triangle against a plane defined by a point and a normal vector.
     *
     * @param plane_p Point on the plane.
     * @param plane_n Normal vector of the plane.
     * @param out     Array to store the resulting triangles after clipping.
     * @return Number of resulting triangles after clipping.
     */
    public int triangleClipAgainstPlane(Vec3D plane_p, Vec3D plane_n, Triangle[] out)
    {
        plane_n = plane_n.normalizeVector();

        Vec3D[] inside_points = {new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0)};
        int nInsidePointCount = 0;

        Vec3D[] outside_points = {new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0)};
        int nOutsidePointCount = 0;

        Vec2D[] inside_tex = {new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0)};
        int nInsideTexCount = 0;

        Vec2D[] outside_tex = {new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0)};
        int nOutsideTexCount = 0;


        double d0 = this.vec3D.dist(plane_n, plane_p);
        double d1 = this.vec3D2.dist(plane_n, plane_p);
        double d2 = this.vec3D3.dist(plane_n, plane_p);

        if (d0 >= 0)
        {
            inside_points[nInsidePointCount++] = this.vec3D;
            inside_tex[nInsideTexCount++] = this.vec2D;
        } else
        {
            outside_points[nOutsidePointCount++] = this.vec3D;
            outside_tex[nOutsideTexCount++] = this.vec2D;
        }
        if (d1 >= 0)
        {
            inside_points[nInsidePointCount++] = this.vec3D2;
            inside_tex[nInsideTexCount++] = this.vec2D2;
        } else
        {
            outside_points[nOutsidePointCount++] = this.vec3D2;
            outside_tex[nOutsideTexCount++] = this.vec2D2;
        }
        if (d2 >= 0)
        {
            inside_points[nInsidePointCount++] = this.vec3D3;
            inside_tex[nInsideTexCount++] = this.vec2D3;
        } else
        {
            outside_points[nOutsidePointCount++] = this.vec3D3;
            outside_tex[nOutsideTexCount++] = this.vec2D3;
        }

        if (nInsidePointCount == 0)
        {
            // All points lie on the outside of plane, so clip whole triangle
            // It ceases to exist

            return 0; // No returned triangles are valid
        }

        if (nInsidePointCount == 3)
        {
            // All points lie on the inside of plane, so do nothing
            // and allow the triangle to simply pass through
            out[0] = this;

            return 1; // Just the one returned original triangle is valid
        }

        if (nInsidePointCount == 1)
        {
            out[0].color = this.color;
            out[0].tex = this.tex;
            out[0].dp = this.dp;
            out[0].vec3D = inside_points[0];
            out[0].vec2D = inside_tex[0];

            ExtraData t = new ExtraData(0);

            out[0].vec3D2 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0], t);
            out[0].vec2D2.u = t.t * (outside_tex[0].u - inside_tex[0].u) + inside_tex[0].u;
            out[0].vec2D2.v = t.t * (outside_tex[0].v - inside_tex[0].v) + inside_tex[0].v;
            out[0].vec2D2.w = t.t * (outside_tex[0].w - inside_tex[0].w) + inside_tex[0].w;

            out[0].vec3D3 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[1], t);
            out[0].vec2D3.u = t.t * (outside_tex[1].u - inside_tex[0].u) + inside_tex[0].u;
            out[0].vec2D3.v = t.t * (outside_tex[1].v - inside_tex[0].v) + inside_tex[0].v;
            out[0].vec2D3.w = t.t * (outside_tex[1].w - inside_tex[0].w) + inside_tex[0].w;
            return 1;
        }

        ExtraData t = new ExtraData(0);

        out[0].color = this.color;
        out[0].tex = this.tex;
        out[0].dp = this.dp;
        out[0].vec3D = inside_points[0];
        out[0].vec3D2 = inside_points[1];
        out[0].vec2D = inside_tex[0];
        out[0].vec2D2 = inside_tex[1];

        out[0].vec3D3 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0], t);
        out[0].vec2D3.u = t.t * (outside_tex[0].u - inside_tex[0].u) + inside_tex[0].u;
        out[0].vec2D3.v = t.t * (outside_tex[0].v - inside_tex[0].v) + inside_tex[0].v;
        out[0].vec2D3.w = t.t * (outside_tex[0].w - inside_tex[0].w) + inside_tex[0].w;

        out[1].color = this.color;
        out[1].tex = this.tex;
        out[1].dp = this.dp;

        out[1].vec3D = inside_points[1];
        out[1].vec2D = inside_tex[1];
        out[1].vec3D2 = out[0].vec3D3;
        out[1].vec2D2 = out[0].vec2D3;
        out[1].vec3D3 = vectorIntersectPlane(plane_p, plane_n, inside_points[1], outside_points[0], t);
        out[1].vec2D3.u = t.t * (outside_tex[0].u - inside_tex[1].u) + inside_tex[1].u;
        out[1].vec2D3.v = t.t * (outside_tex[0].v - inside_tex[1].v) + inside_tex[1].v;
        out[1].vec2D3.w = t.t * (outside_tex[0].w - inside_tex[1].w) + inside_tex[1].w;

        return 2;
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
