package org.test.math;

public class Vec3D
{
    public double x, y, z, w;

    public Vec3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Vec3D()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    public Vec3D addVector(Vec3D in)
    {
        return new Vec3D(in.x + this.x, in.y + this.y, in.z + this.z);
    }

    public Vec3D subtractVector(Vec3D in)
    {
        return new Vec3D(this.x - in.x, this.y - in.y, this.z - in.z);
    }

    public Vec3D multiplyVector(double f)
    {
        return new Vec3D(this.x * f, this.y * f, this.z * f);
    }

    public Vec3D divideVector(double f)
    {
        return new Vec3D(this.x / f, this.y / f, this.z / f);
    }

    public double dotProduct(Vec3D in)
    {
        return this.x * in.x + this.y * in.y + this.z * in.z;
    }

    public double length()
    {
        return Math.sqrt(this.dotProduct(this));
    }

    public Vec3D normalizeVector()
    {
        double f = this.length();
        return this.divideVector(f);
    }

    public Vec3D crossProduct(Vec3D in)
    {
        Vec3D out = new Vec3D(0, 0, 0);

        out.x = this.y * in.z - this.z * in.y;
        out.y = this.z * in.x - this.x * in.z;
        out.z = this.x * in.y - this.y * in.x;

        return out;
    }

    public Vec3D rotateX(double angle)
    {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vec3D(x, cosA * y - sinA * z, sinA * y + cosA * z);
    }

    public Vec3D rotateY(double angle)
    {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vec3D(cosA * x + sinA * z, y, -sinA * x + cosA * z);
    }

    public double dist(Vec3D plane_n, Vec3D plane_p)
    {
        return (plane_n.x * this.x + plane_n.y * this.y + plane_n.z * this.z - plane_n.dotProduct(plane_p));
    }

    public Vec3D vectorIntersectPlane(Vec3D plane_p, Vec3D plane_n, Vec3D lineStart, Vec3D lineEnd, ExtraData tt)
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

    public int triangleClipAgainstPlane(Vec3D plane_p, Vec3D plane_n, Triangle in, Triangle[] out)
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


        double d0 = in.vec3D.dist(plane_n, plane_p);
        double d1 = in.vec3D2.dist(plane_n, plane_p);
        double d2 = in.vec3D3.dist(plane_n, plane_p);

        if (d0 >= 0)
        {
            inside_points[nInsidePointCount++] = in.vec3D;
            inside_tex[nInsideTexCount++] = in.vec2D;
        } else
        {
            outside_points[nOutsidePointCount++] = in.vec3D;
            outside_tex[nOutsideTexCount++] = in.vec2D;
        }
        if (d1 >= 0)
        {
            inside_points[nInsidePointCount++] = in.vec3D2;
            inside_tex[nInsideTexCount++] = in.vec2D2;
        } else
        {
            outside_points[nOutsidePointCount++] = in.vec3D2;
            outside_tex[nOutsideTexCount++] = in.vec2D2;
        }
        if (d2 >= 0)
        {
            inside_points[nInsidePointCount++] = in.vec3D3;
            inside_tex[nInsideTexCount++] = in.vec2D3;
        } else
        {
            outside_points[nOutsidePointCount++] = in.vec3D3;
            outside_tex[nOutsideTexCount++] = in.vec2D3;
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
            out[0] = in;

            return 1; // Just the one returned original triangle is valid
        }

        if (nInsidePointCount == 1 && nOutsidePointCount == 2)
        {
            out[0].color = in.color;
            out[0].tex = in.tex;
            out[0].dp = in.dp;
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

        if (nInsidePointCount == 2 && nOutsidePointCount == 1)
        {
            ExtraData t = new ExtraData(0);

            out[0].color = in.color;
            out[0].tex = in.tex;
            out[0].dp = in.dp;
            out[0].vec3D = inside_points[0];
            out[0].vec3D2 = inside_points[1];
            out[0].vec2D = inside_tex[0];
            out[0].vec2D2 = inside_tex[1];

            out[0].vec3D3 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0], t);
            out[0].vec2D3.u = t.t * (outside_tex[0].u - inside_tex[0].u) + inside_tex[0].u;
            out[0].vec2D3.v = t.t * (outside_tex[0].v - inside_tex[0].v) + inside_tex[0].v;
            out[0].vec2D3.w = t.t * (outside_tex[0].w - inside_tex[0].w) + inside_tex[0].w;

            out[1].color = in.color;
            out[1].tex = in.tex;
            out[1].dp = in.dp;

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

        return 0;
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
