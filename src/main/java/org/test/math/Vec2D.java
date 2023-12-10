package org.test.math;

public class Vec2D
{
    public double u, v, w;

    public Vec2D(double u, double v)
    {
        this.u = u;
        this.v = v;
        this.w = 1;
    }

    public Vec2D()
    {
        this.u = 0;
        this.v = 0;
        this.w = 1;
    }

    @Override
    public String toString()
    {
        return "Vec2D{" +
                "u=" + u +
                ", v=" + v +
                ", w=" + w +
                '}';
    }

    public void scale(double f, boolean multiply)
    {
        if(multiply)
        {
            this.u *= f;
            this.v *= f;
        }
        else
        {
            this.u /= v;
            this.v /= v;
        }
    }

    @Override
    public Object clone()
    {
        return new Vec2D(u, v);
    }
}
