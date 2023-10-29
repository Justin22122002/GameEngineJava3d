package org.test.math;

import java.util.Arrays;

public class Matrix
{
    // should be a 4 * 4 Matrix
    double[][] matrix;

    public Matrix(double[][] matrix)
    {
        this.matrix = matrix;
    }

    public Matrix()
    {
    }

    public Vec3D multiplyMatrixVector(Vec3D in, Matrix m)
    {
        Vec3D out = new Vec3D(0, 0, 0);

        out.x = in.x * m.matrix[0][0] + in.y * m.matrix[1][0] + in.z * m.matrix[2][0] + m.matrix[3][0];
        out.y = in.x * m.matrix[0][1] + in.y * m.matrix[1][1] + in.z * m.matrix[2][1] + m.matrix[3][1];
        out.z = in.x * m.matrix[0][2] + in.y * m.matrix[1][2] + in.z * m.matrix[2][2] + m.matrix[3][2];

        double w = in.x * m.matrix[0][3] + in.y * m.matrix[1][3] + in.z * m.matrix[2][3] + m.matrix[3][3];

        // If w is not 1, normalize the result
        if (w != 1.0)
        {
            out.x /= w;
            out.y /= w;
            out.z /= w;
        }

        return out;
    }

    /**
     * public Matrix projektionMatrix(double fNear, double fFar, double a, double fov)
     * {
     * Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     * <p>
     * double fFov = 1.0 / Math.tan(fov * 0.5 / Math.PI * 180.0);
     * <p>
     * mat.matrix[0][0] = a * fFov;
     * mat.matrix[1][1] = fFov;
     * mat.matrix[2][2] = fFar / (fFar - fNear);
     * mat.matrix[3][2] = 1.0;
     * mat.matrix[2][3] = (-fFar * fNear) / (fFar - fNear);
     * mat.matrix[3][3] = 0;
     * <p>
     * return mat;
     * }
     */

    public Matrix projektionMatrix(double fNear, double fFar, double a, double fov)
    {
        double fFov = 1.0 / Math.tan(fov * 0.5 / Math.PI * 180.0);

        double[][] matrix = new double[][]
                {
                        {a * fFov, 0, 0, 0},
                        {0, fFov, 0, 0},
                        {0, 0, fFar / (fFar - fNear), (- fFar * fNear) / (fFar - fNear)},
                        {0, 0, 1.0, 0}
                };

        return new Matrix(matrix);
    }


    /**
     * public Matrix rotateMatrixX(double angle)
     * {
     * Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     * <p>
     * mat.matrix[0][0] = 1.0;
     * mat.matrix[1][1] = Math.cos(angle);
     * mat.matrix[1][2] = Math.sin(angle);
     * mat.matrix[2][1] = -Math.sin(angle);
     * mat.matrix[2][2] = Math.cos(angle);
     * mat.matrix[3][3] = 1.0;
     * <p>
     * return mat;
     * }
     */
    public Matrix rotateMatrixX(double angle)
    {
        double[][] matrix = new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, Math.cos(angle), -Math.sin(angle), 0},
                        {0, Math.sin(angle), Math.cos(angle), 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix(matrix);
    }

    /**
     * public Matrix rotateMatrixY(double angle)
     * {
     * Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     * <p>
     * mat.matrix[0][0] = Math.cos(angle);
     * mat.matrix[0][2] = Math.sin(angle);
     * mat.matrix[2][0] = -Math.sin(angle);
     * mat.matrix[1][1] = 1.0;
     * mat.matrix[2][2] = Math.cos(angle);
     * mat.matrix[3][3] = 1.0;
     * <p>
     * return mat;
     * }
     */
    public Matrix rotateMatrixY(double angle)
    {
        double[][] matrix = new double[][]
                {
                        {Math.cos(angle), 0, Math.sin(angle), 0},
                        {0, 1.0, 0, 0},
                        {-Math.sin(angle), 0, Math.cos(angle), 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix(matrix);
    }


    /**
     * public Matrix rotateMatrixZ(double angle)
     * {
     * Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     * <p>
     * mat.matrix[0][0] = Math.cos(angle);
     * mat.matrix[0][1] = Math.sin(angle);
     * mat.matrix[1][0] = -Math.sin(angle);
     * mat.matrix[1][1] = Math.cos(angle);
     * mat.matrix[2][2] = 1.0;
     * mat.matrix[3][3] = 1.0;
     * <p>
     * return mat;
     * }
     */
    public Matrix rotateMatrixZ(double angle)
    {
        double[][] matrix = new double[][]
                {
                        {Math.cos(angle), -Math.sin(angle), 0, 0},
                        {Math.sin(angle), Math.cos(angle), 0, 0},
                        {0, 0, 1.0, 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix(matrix);
    }

    /**
     * public Matrix identityMatrix()
     * {
     * Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     * <p>
     * mat.matrix[0][0] = 1.0;
     * mat.matrix[1][1] = 1.0;
     * mat.matrix[2][2] = 1.0;
     * mat.matrix[3][3] = 1.0;
     * <p>
     * return mat;
     * }
     */
    public Matrix identityMatrix()
    {
        double[][] matrix = new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, 1.0, 0, 0},
                        {0, 0, 1.0, 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix(matrix);
    }


    /**
     * public Matrix translationMatrix(double x, double y, double z)
     * {
     * Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     * <p>
     * mat.matrix[0][0] = 1.0;
     * mat.matrix[1][1] = 1.0;
     * mat.matrix[2][2] = 1.0;
     * mat.matrix[3][3] = 1.0;
     * mat.matrix[3][0] = x;
     * mat.matrix[3][1] = y;
     * mat.matrix[3][2] = z;
     * <p>
     * return mat;
     * }
     */
    public Matrix translationMatrix(double x, double y, double z)
    {
        double[][] matrix = new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, 1.0, 0, 0},
                        {0, 0, 1.0, 0},
                        {x, y, z, 1.0}
                };

        return new Matrix(matrix);
    }

    public Matrix matrixMatrixMultiplication(Matrix m1, Matrix m2)
    {
        Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});

        int m1Rows = m1.matrix.length;
        int m1Cols = m1.matrix[0].length;
        int m2Cols = m2.matrix[0].length;

        for (int i = 0; i < m1Rows; i++)
        {
            for (int j = 0; j < m2Cols; j++)
            {
                for (int k = 0; k < m1Cols; k++)
                {
                    mat.matrix[i][j] += m1.matrix[i][k] * m2.matrix[k][j];
                }
            }
        }

        return mat;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("Matrix{\n");

        for (double[] row : matrix)
        {
            result.append(Arrays.toString(row)).append("\n");
        }

        result.append("}");

        return result.toString();
    }
}
