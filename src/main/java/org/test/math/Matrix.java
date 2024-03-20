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

    public Vec3D multiplyMatrixVector(Vec3D in)
    {
        Vec3D out = new Vec3D();

        out.x = in.x * this.matrix[0][0] + in.y * this.matrix[1][0] + in.z * this.matrix[2][0] + in.w * this.matrix[3][0];
        out.y = in.x * this.matrix[0][1] + in.y * this.matrix[1][1] + in.z * this.matrix[2][1] + in.w * this.matrix[3][1];
        out.z = in.x * this.matrix[0][2] + in.y * this.matrix[1][2] + in.z * this.matrix[2][2] + in.w * this.matrix[3][2];
        out.w = in.x * this.matrix[0][3] + in.y * this.matrix[1][3] + in.z * this.matrix[2][3] + in.w * this.matrix[3][3];

        return out;
    }

    public static Matrix projektionMatrix(double fNear, double fFar, double a, double fov)
    {
        double fFov = 1.0 / Math.tan(fov * 0.5 / Math.PI * 180.0);

        double[][] matrix = new double[][]
                {
                        {a * fFov, 0, 0, 0},
                        {0, fFov, 0, 0},
                        {0, 0, fFar / (fFar - fNear), (-fFar * fNear) / (fFar - fNear)},
                        {0, 0, 1.0, 0}
                };

        return new Matrix(matrix);
    }

    public static Matrix rotateMatrixX(double angle)
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

    public static Matrix rotateMatrixY(double angle)
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

    public static Matrix rotateMatrixZ(double angle)
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

    public static Matrix identityMatrix()
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

    public static Matrix translationMatrix(double x, double y, double z)
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

    public Matrix matrixMatrixMultiplication(Matrix m)
    {
        Matrix mat = new Matrix(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});

        int m1Rows = m.matrix.length;
        int m1Cols = m.matrix[0].length;
        int m2Cols = this.matrix[0].length;

        for (int i = 0; i < m1Rows; i++)
        {
            for (int j = 0; j < m2Cols; j++)
            {
                for (int k = 0; k < m1Cols; k++)
                {
                    mat.matrix[i][j] += m.matrix[i][k] * this.matrix[k][j];
                }
            }
        }

        return mat;
    }

    public Matrix pointAtMatrix(Vec3D pos, Vec3D target, Vec3D up)
    {
        // CALCULATE THE NEW FORWARD DIRECTION
        Vec3D newForward = new Vec3D(0, 0, 0);

        newForward = target.subtractVector(pos);
        newForward = newForward.normalizeVector();

        // CALCULATE THE NEW UP DIRECTION
        Vec3D a = new Vec3D(0, 0, 0);
        Vec3D newUp;

        a = newForward.multiplyVector(up.dotProduct(newForward));
        newUp = up.subtractVector(a);
        newUp = newUp.normalizeVector();

        // NEW RIGHT DIRECTION JUST TAKES THE CROSS PRODUCT
        Vec3D newRight;
        newRight = newUp.crossProduct(newForward);

        // MANUALLY CONSTRUCT POINT AT MATRIX, THE DIMENSION AND TRANSITION
        double[][] matrix = new double[][]
                {
                        {newRight.x, newRight.y, newRight.z, 0},
                        {newUp.x, newUp.y, newUp.z, 0},
                        {newForward.x, newForward.y, newForward.z, 0},
                        {pos.x, pos.y, pos.z, 1.0}
                };

        return new Matrix(matrix);
    }


    public Matrix inverseMatrix()
    {
        return new Matrix(new double[][]
                {
                        {this.matrix[0][0], this.matrix[1][0], this.matrix[2][0], 0.0},
                        {this.matrix[0][1], this.matrix[1][1], this.matrix[2][1], 0.0},
                        {this.matrix[0][2], this.matrix[1][2], this.matrix[2][2], 0.0},
                        {
                                -(this.matrix[3][0] * this.matrix[0][0] + this.matrix[3][1] * this.matrix[0][1] + this.matrix[3][2] * this.matrix[0][2]),
                                -(this.matrix[3][0] * this.matrix[1][0] + this.matrix[3][1] * this.matrix[1][1] + this.matrix[3][2] * this.matrix[1][2]),
                                -(this.matrix[3][0] * this.matrix[2][0] + this.matrix[3][1] * this.matrix[2][1] + this.matrix[3][2] * this.matrix[2][2]),
                                1.0
                        }
                });
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
