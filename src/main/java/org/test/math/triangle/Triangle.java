package org.test.math.triangle;

import org.test.graphics.Texture;
import org.test.math.ExtraData;
import org.test.math.vector.Vector2D;
import org.test.math.vector.Vector3D;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static org.test.math.GeometryUtils.vectorIntersectPlane;

/**
 * Represents a triangle in 3D space.
 */
public class Triangle implements Cloneable
{
    // Point Coordinates
    public Vector3D vec3D, vec3D2, vec3D3;
    //Texture Coordinates
    public Vector2D vec2D, vec2D2, vec2D3;
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
    public Triangle(Vector3D vec3D, Vector3D vec3D2, Vector3D vec3D3)
    {
        this.vec3D = vec3D;
        this.vec3D2 = vec3D2;
        this.vec3D3 = vec3D3;
        this.vec2D = new Vector2D();
        this.vec2D2 = new Vector2D();
        this.vec2D3 = new Vector2D();
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
    public Triangle(Vector3D vec3D, Vector3D vec3D2, Vector3D vec3D3, Vector2D vec2D, Vector2D vec2D2, Vector2D vec2D3)
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
        this.vec3D = new Vector3D();
        this.vec3D2 = new Vector3D();
        this.vec3D3 = new Vector3D();
        this.vec2D = new Vector2D();
        this.vec2D2 = new Vector2D();
        this.vec2D3 = new Vector2D();
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
    public int triangleClipAgainstPlane(Vector3D plane_p, Vector3D plane_n, Triangle[] out)
    {
        plane_n = plane_n.normalizeVector();

        Vector3D[] inside_points = {new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), new Vector3D(0, 0, 0)};
        int nInsidePointCount = 0;

        Vector3D[] outside_points = {new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), new Vector3D(0, 0, 0)};
        int nOutsidePointCount = 0;

        Vector2D[] inside_tex = {new Vector2D(0, 0), new Vector2D(0, 0), new Vector2D(0, 0)};
        int nInsideTexCount = 0;

        Vector2D[] outside_tex = {new Vector2D(0, 0), new Vector2D(0, 0), new Vector2D(0, 0)};
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

    public static Triangle[] getNearestPlane()
    {
        return new Triangle[]
                {
                        new Triangle
                                (
                                        new Vector3D(0, 0, 0),
                                        new Vector3D(0, 0, 0),
                                        new Vector3D(0, 0, 0),
                                        new Vector2D(0, 0),
                                        new Vector2D(0, 0),
                                        new Vector2D(0, 0)
                                ),
                        new Triangle
                                (
                                        new Vector3D(0, 0, 0),
                                        new Vector3D(0, 0, 0),
                                        new Vector3D(0, 0, 0),
                                        new Vector2D(0, 0),
                                        new Vector2D(0, 0),
                                        new Vector2D(0, 0)
                                )
                };
    }

    @Override
    public String toString()
    {
        return "Triangle{" +
                "vec3D=" + vec3D +
                ", vec3D2=" + vec3D2 +
                ", vec3D3=" + vec3D3 +
                ", vec2D=" + vec2D +
                ", vec2D2=" + vec2D2 +
                ", vec2D3=" + vec2D3 +
                ", color=" + color +
                ", dp=" + dp +
                ", tex=" + tex +
                '}';
    }

    @Override
    public Triangle clone()
    {
        try
        {
            Triangle clone = (Triangle) super.clone();
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
        Triangle triangle = (Triangle) o;
        return Objects.equals(vec3D, triangle.vec3D)
                && Objects.equals(vec3D2, triangle.vec3D2)
                && Objects.equals(vec3D3, triangle.vec3D3)
                && Objects.equals(vec2D, triangle.vec2D)
                && Objects.equals(vec2D2, triangle.vec2D2)
                && Objects.equals(vec2D3, triangle.vec2D3);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(vec3D, vec3D2, vec3D3, vec2D, vec2D2, vec2D3);
    }
}
