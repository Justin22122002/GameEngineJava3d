package org.test.math.matrix;

import org.junit.jupiter.api.Test;
import org.test.math.vector.Vector3D;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4x4Test
{
    @Test
    void multiplyMatrixVector()
    {
        Matrix4x4 matrix = new Matrix4x4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        Vector3D vector = new Vector3D(1, 2, 3);
        Vector3D result = matrix.multiplyMatrixVector(vector);

        Vector3D correct = new Vector3D(51, 58, 65);
        correct.w = 72;

        assertEquals(result, correct);
    }

    @Test
    void projektionMatrix()
    {
        double fNear = 1; double fFar = 10; double a = 1; double fov = Math.PI / 4; double fFov = 1.0 / Math.tan(fov * 0.5 / Math.PI * 180.0);

        Matrix4x4 matrix = Matrix4x4.projektionMatrix(1, 10, 1, Math.PI / 4);

        Matrix4x4 correctMatrix = new Matrix4x4(new double[][]
                {
                        {a * fFov, 0, 0, 0},
                        {0, fFov, 0, 0},
                        {0, 0, fFar / (fFar - fNear), (-fFar * fNear) / (fFar - fNear)},
                        {0, 0, 1.0, 0}
                });

        assertEquals(matrix, correctMatrix);
    }

    @Test
    void rotateMatrixX()
    {
        final double angle = Math.PI / 4;
        Matrix4x4 matrix = Matrix4x4.rotateMatrixX(angle);

        Matrix4x4 correctMatrix = new Matrix4x4(new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, Math.cos(angle), -Math.sin(angle), 0},
                        {0, Math.sin(angle), Math.cos(angle), 0},
                        {0, 0, 0, 1.0}
                });

        assertEquals(matrix, correctMatrix);
    }

    @Test
    void rotateMatrixY()
    {
        final double angle = Math.PI / 4;
        Matrix4x4 matrix = Matrix4x4.rotateMatrixY(angle);

        Matrix4x4 correctMatrix = new Matrix4x4(new double[][]
                {
                        {Math.cos(angle), 0, Math.sin(angle), 0},
                        {0, 1.0, 0, 0},
                        {-Math.sin(angle), 0, Math.cos(angle), 0},
                        {0, 0, 0, 1.0}
                });

        assertEquals(matrix, correctMatrix);
    }

    @Test
    void rotateMatrixZ()
    {
        final double angle = Math.PI / 4;
        Matrix4x4 matrix = Matrix4x4.rotateMatrixZ(angle);

        Matrix4x4 correctMatrix = new Matrix4x4(new double[][]
                {
                        {Math.cos(angle), -Math.sin(angle), 0, 0},
                        {Math.sin(angle), Math.cos(angle), 0, 0},
                        {0, 0, 1.0, 0},
                        {0, 0, 0, 1.0}
                });

        assertEquals(matrix, correctMatrix);
    }

    @Test
    void identityMatrix()
    {
        Matrix4x4 matrix = Matrix4x4.identityMatrix();

        assertEquals(1.0, matrix.matrix[0][0]);
        assertEquals(0.0, matrix.matrix[0][1]);
        assertEquals(0.0, matrix.matrix[0][2]);
        assertEquals(0.0, matrix.matrix[0][3]);

        // Add more assertions as needed...
    }

    @Test
    void translationMatrix()
    {
        Matrix4x4 matrix = Matrix4x4.translationMatrix(1, 2, 3);

        assertEquals(1.0, matrix.matrix[3][0]);
        assertEquals(2.0, matrix.matrix[3][1]);
        assertEquals(3.0, matrix.matrix[3][2]);
        assertEquals(1.0, matrix.matrix[3][3]);

        // Add more assertions as needed...
    }

    @Test
    void multiplyMatrix()
    {
        Matrix4x4 matrix1 = new Matrix4x4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        Matrix4x4 matrix2 = new Matrix4x4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        Matrix4x4 result = matrix1.multiplyMatrix(matrix2);

        Matrix4x4 rightResult = new Matrix4x4(new double[][]{
                {90, 100, 110, 120},
                {202, 228, 254, 280},
                {314, 356, 398, 440},
                {426, 484, 542, 600}
        });

        assertEquals(rightResult, result);
    }

    @Test
    void pointAtMatrix()
    {
        Matrix4x4 matrix = new Matrix4x4();
        Vector3D pos = new Vector3D(1, 2, 3);
        Vector3D target = new Vector3D(4, 5, 6);
        Vector3D up = new Vector3D(7, 8, 9);

        Matrix4x4 result = matrix.pointAtMatrix(pos, target, up);

        assertEquals(1, result.matrix[3][0]);
        assertEquals(2, result.matrix[3][1]);
        assertEquals(3, result.matrix[3][2]);
    }

    @Test
    void inverseMatrix()
    {
        Matrix4x4 matrix = new Matrix4x4(new double[][]{
                {1, 0, 2, -1},
                {3, -2, 1, 5},
                {2, 1, 4, 0},
                {-1, 2, 3, -2}
        });

        matrix.inverseMatrix();
    }

    @Test
    void testClone()
    {
        Matrix4x4 matrix = new Matrix4x4(new double[][]{
                {1, 0, 2, -1},
                {3, -2, 1, 5},
                {2, 1, 4, 0},
                {-1, 2, 3, -2}
        });

        Matrix4x4 clonedMatrix = matrix.clone();

        assertEquals(matrix.toString(), clonedMatrix.toString());
        assertNotSame(matrix, clonedMatrix);
    }
}