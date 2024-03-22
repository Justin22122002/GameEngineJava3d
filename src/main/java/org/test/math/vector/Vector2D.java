package org.test.math.vector;

import java.util.Objects;

/**
 * A simple 2D vector class representing vectors in a two-dimensional space.
 */
public class Vector2D implements Cloneable
{
    public double u, v, w;

    /**
     * Constructs a Vector2D with the specified components.
     *
     * @param u The x-component of the vector.
     * @param v The y-component of the vector.
     */
    public Vector2D(double u, double v)
    {
        this.u = u;
        this.v = v;
        this.w = 1;
    }

    /**
     * Constructs a Vector2D representing the origin (0,0).
     */
    public Vector2D()
    {
        this.u = 0;
        this.v = 0;
        this.w = 1;
    }

    /**
     * Returns a string representation of the vector.
     *
     * @return A string representing the vector.
     */
    @Override
    public String toString()
    {
        return "Vector2D{" +
                "u=" + u +
                ", v=" + v +
                ", w=" + w +
                '}';
    }

    /**
     * Scales the vector by the specified factor.
     *
     * @param f        The scaling factor.
     * @param multiply If true, scale the vector by multiplication. If false, scale by division.
     */
    public void scale(double f, boolean multiply)
    {
        if(multiply)
        {
            this.u *= f;
            this.v *= f;
        }
        else
        {
            this.u /= f;
            this.v /= f;
        }
    }

    @Override
    public Vector2D clone()
    {
        try
        {
            Vector2D clone = (Vector2D) super.clone();
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
        Vector2D vector2D = (Vector2D) o;
        return Double.compare(u, vector2D.u) == 0
                && Double.compare(v, vector2D.v) == 0
                && Double.compare(w, vector2D.w) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(u, v, w);
    }
}
