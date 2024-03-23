package org.test.math.matrix;

import org.test.math.vector.Vector3D;

import java.util.Arrays;

/**
 * Represents a 4x4 matrix.
 */
public class Matrix4x4 implements Cloneable
{
    // should be a 4 * 4 Matrix
    double[][] matrix;

    /**
     * Constructs a new Matrix4x4 with the given matrix data.
     *
     * @param matrix The 4x4 matrix data.
     */
    public Matrix4x4(double[][] matrix)
    {
        this.matrix = matrix;
    }

    /**
     * Constructs a new empty Matrix4x4.
     */
    public Matrix4x4()
    {
    }

    /**
     * Multiplies a vector by this matrix.
     *
     * @param in The input vector.
     * @return The resulting vector after multiplication.
     */
    public Vector3D multiplyMatrixVector(Vector3D in)
    {
        Vector3D out = new Vector3D();

        out.x = in.x * this.matrix[0][0] + in.y * this.matrix[1][0] + in.z * this.matrix[2][0] + in.w * this.matrix[3][0];
        out.y = in.x * this.matrix[0][1] + in.y * this.matrix[1][1] + in.z * this.matrix[2][1] + in.w * this.matrix[3][1];
        out.z = in.x * this.matrix[0][2] + in.y * this.matrix[1][2] + in.z * this.matrix[2][2] + in.w * this.matrix[3][2];
        out.w = in.x * this.matrix[0][3] + in.y * this.matrix[1][3] + in.z * this.matrix[2][3] + in.w * this.matrix[3][3];

        return out;
    }

    /**
     * Creates a projection matrix based on the given parameters.
     *
     * @param fNear The distance to the near plane.
     * @param fFar  The distance to the far plane.
     * @param a     The aspect ratio.
     * @param fov   The field of view angle in degrees.
     * @return This Matrix4x4 instance with the projection matrix.
     */
    public static Matrix4x4 projektionMatrix(double fNear, double fFar, double a, double fov)
    {
        double fFov = 1.0 / Math.tan(fov * 0.5 / Math.PI * 180.0);

        double[][] matrix = new double[][]
                {
                        {a * fFov, 0, 0, 0},
                        {0, fFov, 0, 0},
                        {0, 0, fFar / (fFar - fNear), (-fFar * fNear) / (fFar - fNear)},
                        {0, 0, 1.0, 0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Creates a rotation matrix around the X-axis based on the given angle.
     *
     * @param angle The rotation angle in radians around the X-axis.
     * @return This Matrix4x4 instance with the rotation matrix.
     */
    public static Matrix4x4 rotateMatrixX(double angle)
    {
        double[][] matrix = new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, Math.cos(angle), Math.sin(angle), 0},
                        {0, -Math.sin(angle), Math.cos(angle), 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Creates a rotation matrix around the Y-axis based on the given angle.
     *
     * @param angle The rotation angle in radians around the Y-axis.
     * @return This Matrix4x4 instance with the rotation matrix.
     */
    public static Matrix4x4 rotateMatrixY(double angle)
    {
        double[][] matrix = new double[][]
                {
                        {Math.cos(angle), 0, Math.sin(angle), 0},
                        {0, 1.0, 0, 0},
                        {-Math.sin(angle), 0, Math.cos(angle), 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Creates a rotation matrix around the Z-axis based on the given angle.
     *
     * @param angle The rotation angle in radians around the Z-axis.
     * @return This Matrix4x4 instance with the rotation matrix.
     */
    public static Matrix4x4 rotateMatrixZ(double angle)
    {
        double[][] matrix = new double[][]
                {
                        {Math.cos(angle), Math.sin(angle), 0, 0},
                        {-Math.sin(angle), Math.cos(angle), 0, 0},
                        {0, 0, 1.0, 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Creates an identity matrix.
     *
     * @return This Matrix4x4 instance with the identity matrix.
     */
    public static Matrix4x4 identityMatrix()
    {
        double[][] matrix = new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, 1.0, 0, 0},
                        {0, 0, 1.0, 0},
                        {0, 0, 0, 1.0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Creates a translation matrix based on the given translations.
     *
     * @param x The translation in the x direction.
     * @param y The translation in the y direction.
     * @param z The translation in the z direction.
     * @return This Matrix4x4 instance with the translation matrix.
     */
    public static Matrix4x4 translationMatrix(double x, double y, double z)
    {
        double[][] matrix = new double[][]
                {
                        {1.0, 0, 0, 0},
                        {0, 1.0, 0, 0},
                        {0, 0, 1.0, 0},
                        {x, y, z, 1.0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Multiplies this matrix by another matrix and returns the result.
     *
     * @param m The matrix to be multiplied with this matrix.
     * @return The resulting matrix of the multiplication.
     */
    public Matrix4x4 multiplyMatrix(Matrix4x4 m)
    {
        Matrix4x4 mat = new Matrix4x4(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});

        int m1Rows = m.matrix.length;
        int m1Cols = m.matrix[0].length;
        int m2Cols = this.matrix[0].length;

        for (int i = 0; i < m1Rows; i++)
        {
            for (int j = 0; j < m2Cols; j++)
            {
                for (int k = 0; k < m1Cols; k++)
                {
                    mat.matrix[i][j] += this.matrix[i][k] * m.matrix[k][j];
                }
            }
        }

        return mat;
    }

    /**
     * Constructs a matrix that represents a transformation to align an object's orientation from a given position to a target position,
     * with a specified up vector.
     *
     * @param pos    The current position of the object.
     * @param target The target position where the object should be oriented towards.
     * @param up     The up vector specifying the orientation of the object.
     * @return The transformation matrix.
     */
    public Matrix4x4 pointAtMatrix(Vector3D pos, Vector3D target, Vector3D up)
    {
        // CALCULATE THE NEW FORWARD DIRECTION
        Vector3D newForward = target.subtractVector(pos);
        newForward = newForward.normalizeVector();

        // CALCULATE THE NEW UP DIRECTION
        Vector3D a = newForward.multiplyVector(up.dotProduct(newForward));
        Vector3D newUp = up.subtractVector(a);
        newUp = newUp.normalizeVector();

        // NEW RIGHT DIRECTION JUST TAKES THE CROSS PRODUCT
        Vector3D newRight;
        newRight = newUp.crossProduct(newForward);

        // MANUALLY CONSTRUCT POINT AT MATRIX, THE DIMENSION AND TRANSITION
        double[][] matrix = new double[][]
                {
                        {newRight.x, newRight.y, newRight.z, 0},
                        {newUp.x, newUp.y, newUp.z, 0},
                        {newForward.x, newForward.y, newForward.z, 0},
                        {pos.x, pos.y, pos.z, 1.0}
                };

        return new Matrix4x4(matrix);
    }

    /**
     * Calculates the inverse of this matrix.
     *
     * @return The inverse of this matrix.
     * @throws ArithmeticException if the matrix is singular and does not have an inverse.
     */
    public Matrix4x4 inverseMatrix()
    {
        return new Matrix4x4(new double[][]
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
    /**
     *          Matrix4x4 m = new Matrix4x4(new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}});
     *         m.matrix[0][0] = this.matrix[0][0]; m.matrix[0][1] = this.matrix[1][0]; m.matrix[0][2] = this.matrix[2][0]; m.matrix[0][3] = 0.0f;
     *         m.matrix[1][0] = this.matrix[0][1]; m.matrix[1][1] = this.matrix[1][1]; m.matrix[1][2] = this.matrix[2][1]; m.matrix[1][3] = 0.0f;
     *         m.matrix[2][0] = this.matrix[0][2]; m.matrix[2][1] = this.matrix[1][2]; m.matrix[2][2] = this.matrix[2][2]; m.matrix[2][3] = 0.0f;
     *         m.matrix[3][0] = -(this.matrix[3][0] * m.matrix[0][0] + this.matrix[3][1] * m.matrix[1][0] + this.matrix[3][2] * m.matrix[2][0]);
     *         m.matrix[3][1] = -(this.matrix[3][0] * m.matrix[0][1] + this.matrix[3][1] * m.matrix[1][1] + this.matrix[3][2] * m.matrix[2][1]);
     *         m.matrix[3][2] = -(this.matrix[3][0] * m.matrix[0][2] + this.matrix[3][1] * m.matrix[1][2] + this.matrix[3][2] * m.matrix[2][2]);
     *         m.matrix[3][3] = 1.0f;
     *         return m;
     */

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


    @Override
    public Matrix4x4 clone()
    {
        try
        {
            Matrix4x4 clone = (Matrix4x4) super.clone();
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
        Matrix4x4 matrix4x4 = (Matrix4x4) o;
        return Arrays.deepEquals(matrix, matrix4x4.matrix);
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(matrix);
    }
}
