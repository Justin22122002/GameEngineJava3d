package org.test.renderer;

import org.test.math.matrix.Matrix4x4;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;

import java.util.Arrays;

import static org.test.renderer.PanelConfig.getImageHeight;
import static org.test.renderer.PanelConfig.getImageWidth;

/**
 * The RasterAssembler class provides methods for assembling, transforming, and projecting triangles for rendering.
 */
public class RasterAssembler
{
    /**
     * Assembles the world matrix for a triangle.
     *
     * @param triTrans  The transformed triangle.
     * @param tri       The original triangle.
     * @param matWorld  The world matrix.
     */
    public void assembleWorldMatrix(Triangle triTrans, Triangle tri, Matrix4x4 matWorld)
    {
        test(triTrans, tri, matWorld);
    }

    private void test(Triangle triTrans, Triangle tri, Matrix4x4 matWorld)
    {
        triTrans.vec3D = matWorld.multiplyMatrixVector(tri.vec3D);
        triTrans.vec3D2 = matWorld.multiplyMatrixVector(tri.vec3D2);
        triTrans.vec3D3 = matWorld.multiplyMatrixVector(tri.vec3D3);
        triTrans.vec2D = tri.vec2D;
        triTrans.vec2D2 = tri.vec2D2;
        triTrans.vec2D3 = tri.vec2D3;
    }

    /**
     * Assembles vertex normals for a triangle.
     *
     * @param triTrans The transformed triangle.
     * @param line1    The first line vector.
     * @param line2    The second line vector.
     * @return The calculated normal vector.
     */
    public Vector3D assembleVertexNormals(Triangle triTrans, Vector3D line1, Vector3D line2)
    {
        line1.setVector(triTrans.vec3D2.subtractVector(triTrans.vec3D));
        line2.setVector(triTrans.vec3D3.subtractVector(triTrans.vec3D));

        Vector3D normal = line1.crossProduct(line2);
        normal = normal.normalizeVector();

        return normal;
    }

    /**
     * Assembles the view matrix for a triangle.
     *
     * @param triViewed The viewed triangle.
     * @param triTrans  The transformed triangle.
     * @param matView   The view matrix.
     */
    public void assembleViewMatrix(Triangle triViewed, Triangle triTrans, Matrix4x4 matView)
    {
        test(triViewed, triTrans, matView);
    }

    /**
     * Calculates the view matrix based on camera parameters.
     *
     * @param fPitch    The pitch angle.
     * @param fYaw      The yaw angle.
     * @param camera    The camera object.
     * @return The calculated view matrix.
     */
    public Matrix4x4 calculateViewMatrix(double fPitch, double fYaw, Camera camera)
    {
        Vector3D vUp = new Vector3D(0, 1, 0);
        Vector3D vTarget = new Vector3D(0, 0, 1);
        Matrix4x4 matCameraRot = Matrix4x4.rotateMatrixX(fPitch).multiplyMatrix(Matrix4x4.rotateMatrixY(fYaw));
        
        camera.setvLookDir(matCameraRot.multiplyMatrixVector(vTarget));
        vTarget = camera.getPosition().addVector(camera.getvLookDir());

        // using the information provided above to define a camera matrix
        Matrix4x4 matCamera = new Matrix4x4().pointAtMatrix(camera.getPosition(), vTarget, vUp);

        // matView
        return matCamera.inverseMatrix();
    }

    /**
     * Assembles the projection matrix for a triangle.
     *
     * @param triProjection The projected triangle.
     * @param triViewed     The viewed triangle.
     * @param matProj       The projection matrix.
     */
    public void assembleProjectionMatrix(Triangle triProjection, Triangle triViewed, Matrix4x4 matProj)
    {
        // project 3d to 2d screen
        triProjection.vec3D = matProj.multiplyMatrixVector(triViewed.vec3D);
        triProjection.vec3D2 = matProj.multiplyMatrixVector(triViewed.vec3D2);
        triProjection.vec3D3 = matProj.multiplyMatrixVector(triViewed.vec3D3);
    }

    /**
     * Assembles the clip space coordinates for a triangle.
     *
     * @param triProjection The projected triangle.
     * @param clipped       The clipped triangle array.
     * @param n             The index of the clipped triangle.
     * @param matProj       The projection matrix.
     */
    public void assembleClipSpace(Triangle triProjection, Triangle[] clipped, int n, Matrix4x4 matProj)
    {
        // project 3d geometrical data to normalize 2d screen
        triProjection.vec3D = matProj.multiplyMatrixVector(clipped[n].vec3D);
        triProjection.vec3D2 = matProj.multiplyMatrixVector(clipped[n].vec3D2);
        triProjection.vec3D3 = matProj.multiplyMatrixVector(clipped[n].vec3D3);

        triProjection.vec2D = clipped[n].vec2D.clone();
        triProjection.vec2D2 = clipped[n].vec2D2.clone();
        triProjection.vec2D3 = clipped[n].vec2D3.clone();
    }

    /**
     * Performs perspective divide for a projected triangle.
     *
     * @param triProjection The projected triangle.
     */
    public void assemblePerspectiveDivide(Triangle triProjection)
    {
        // scale texture c
        triProjection.vec2D.u = triProjection.vec2D.u / triProjection.vec3D.w;
        triProjection.vec2D2.u = triProjection.vec2D2.u / triProjection.vec3D2.w;
        triProjection.vec2D3.u = triProjection.vec2D3.u / triProjection.vec3D3.w;
        triProjection.vec2D.v = triProjection.vec2D.v / triProjection.vec3D.w;
        triProjection.vec2D2.v = triProjection.vec2D2.v / triProjection.vec3D2.w;
        triProjection.vec2D3.v = triProjection.vec2D3.v / triProjection.vec3D3.w;

        triProjection.vec2D.w = 1.0d / triProjection.vec3D.w;
        triProjection.vec2D2.w = 1.0d / triProjection.vec3D2.w;
        triProjection.vec2D3.w = 1.0d / triProjection.vec3D3.w;

        // Scale into view, we moved the normalising into cartesian space
        triProjection.vec3D = triProjection.vec3D.divideVector(triProjection.vec3D.w);
        triProjection.vec3D2 = triProjection.vec3D2.divideVector(triProjection.vec3D2.w);
        triProjection.vec3D3 = triProjection.vec3D3.divideVector(triProjection.vec3D3.w);
    }

    /**
     * Scales the projected triangle coordinates into view space.
     *
     * @param triProjection The projected triangle.
     */
    public void scaleIntoView(Triangle triProjection)
    {
        // X/Y are inverted so put them back
        triProjection.vec3D.x *= -1.0;
        triProjection.vec3D2.x *= -1.0;
        triProjection.vec3D3.x *= -1.0;
        triProjection.vec3D.y *= -1.0;
        triProjection.vec3D2.y *= -1.0;
        triProjection.vec3D3.y *= -1.0;

        // Offset verts into visible normalised space
        Vector3D vOffsetView = new Vector3D(1, 1, 0);
        triProjection.vec3D = triProjection.vec3D.addVector(vOffsetView);
        triProjection.vec3D2 = triProjection.vec3D2.addVector(vOffsetView);
        triProjection.vec3D3 = triProjection.vec3D3.addVector(vOffsetView);

        triProjection.vec3D.x *= 0.5 * getImageWidth();
        triProjection.vec3D.y *= 0.5 * getImageHeight();
        triProjection.vec3D2.x *= 0.5 * getImageWidth();
        triProjection.vec3D2.y *= 0.5 * getImageHeight();
        triProjection.vec3D3.x *= 0.5 * getImageWidth();
        triProjection.vec3D3.y *= 0.5 * getImageHeight();
    }
}
