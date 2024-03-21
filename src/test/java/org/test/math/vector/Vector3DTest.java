package org.test.math.vector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector3DTest
{
    @Test
    void addVector()
    {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        Vector3D result = v1.addVector(v2);
        assertEquals(new Vector3D(5, 7, 9), result);
    }

    @Test
    void subtractVector()
    {
        Vector3D v1 = new Vector3D(4, 5, 6);
        Vector3D v2 = new Vector3D(1, 2, 3);
        Vector3D result = v1.subtractVector(v2);
        assertEquals(new Vector3D(3, 3, 3), result);
    }

    @Test
    void multiplyVector()
    {
        Vector3D v = new Vector3D(1, 2, 3);
        Vector3D result = v.multiplyVector(2);
        assertEquals(new Vector3D(2, 4, 6), result);
    }

    @Test
    void divideVector()
    {
        Vector3D v = new Vector3D(6, 8, 10);
        Vector3D result = v.divideVector(2);
        assertEquals(new Vector3D(3, 4, 5), result);
    }

    @Test
    void dotProduct()
    {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        double result = v1.dotProduct(v2);
        assertEquals(32, result);
    }

    @Test
    void length()
    {
        Vector3D v = new Vector3D(1, 2, 2);
        double result = v.length();
        assertEquals(3, result);
    }

    @Test
    void normalizeVector()
    {
        Vector3D v = new Vector3D(2, 2, 2);
        Vector3D result = v.normalizeVector();
        assertEquals(new Vector3D(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3)), result);
    }

    @Test
    void crossProduct()
    {
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);
        Vector3D result = v1.crossProduct(v2);
        assertEquals(new Vector3D(0, 0, 1), result);
    }

    @Test
    void rotateX()
    {
        Vector3D v = new Vector3D(1, 0, 0);
        Vector3D result = v.rotateX(Math.PI / 2);
        assertEquals(new Vector3D(1, 0, 0), result);
    }

    @Test
    void rotateY()
    {
        Vector3D v = new Vector3D(1, 0, 0);
        Vector3D result = v.rotateY(Math.PI / 2);
        assertArrayEquals(new double[]{0, 0, -1}, new double[]{result.x, result.y, result.z}, 1e-15);
    }

    @Test
    void dist()
    {
        Vector3D v = new Vector3D(1, 1, 1);
        Vector3D plane_n = new Vector3D(1, 1, 1);
        Vector3D plane_p = new Vector3D(0, 0, 0);
        double result = v.dist(plane_n, plane_p);
        assertEquals(3, result);
    }
}