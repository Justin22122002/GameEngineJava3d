package org.test.renderer;

import org.test.renderdata.RenderSettings;
import org.test.math.matrix.Matrix4x4;
import org.test.math.triangle.Mesh;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

import static org.test.math.triangle.Triangle.getNearestPlane;

/**
 * The Rasterizer class is responsible for converting geometric primitives into a rasterized image during rendering.
 * It performs various tasks including triangle setup, rasterization, interpolation, depth testing, fragment processing,
 * and framebuffer operations. This class utilizes the RasterAssembler to assist in performing various operations
 * required for rendering. The main purpose is to convert 3D models into 2D objects on the screen.
 *
 * @see RasterAssembler
 */
public class Rasterizer
{
    private final RenderSettings settings = RenderSettings.getInstance();
    private final RasterAssembler rasterAssembler;

    /**
     * Constructs a Rasterizer with the specified RasterAssembler.
     *
     * @param rasterAssembler The RasterAssembler used for various rendering operations.
     */
    public Rasterizer(RasterAssembler rasterAssembler)
    {
        this.rasterAssembler = rasterAssembler;
    }

    /**
     * Rasterizes geometric primitives and returns a list of triangles for rendering.
     *
     * @return A list of triangles to be rasterized.
     */
    public List<Triangle> raster()
    {
        List<Triangle> vecTrianglesToRaster = new ArrayList<>();

        // Rotation matrix
        settings.setMatZ(Matrix4x4.rotateMatrixZ(settings.getvCamera().getfTheta() * 0.5));
        settings.setMatZX(Matrix4x4.rotateMatrixX(settings.getvCamera().getfTheta()));

        // Distance from cube -> translation matrix
        Matrix4x4 trans = Matrix4x4.translationMatrix(0, 0, 1);

        // Matrix multiplication to accumulate multiple transformations
        Matrix4x4 matWorld = settings.getMatZ().multiplyMatrix(settings.getMatZX());
        matWorld = matWorld.multiplyMatrix(trans);

        Matrix4x4 matView = rasterAssembler.calculateViewMatrix(
                settings.getvCamera().getfPitch(),
                settings.getvCamera().getfYaw(),
                settings.getvCamera(),
                settings.getvCamera().getvLookDir()
        );

        for (Mesh mesh : settings.getPolygonGroup().getPolyGroup())
        {
            for (Triangle tri : mesh.triangles)
            {
                Triangle triProjection = new Triangle(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), new Vector3D(0, 0, 0));
                Triangle triTrans = new Triangle(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), new Vector3D(0, 0, 0));
                Triangle triViewed = new Triangle(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), new Vector3D(0, 0, 0));

                // Assemble World Matrix
                rasterAssembler.assembleWorldMatrix(triTrans, tri, matWorld);

                Vector3D line1 = new Vector3D();
                Vector3D line2 = new Vector3D();
                Vector3D normal = rasterAssembler.assembleVertexNormals(triTrans, line1, line2);

                Vector3D vCameraRay = triTrans.vec3D.subtractVector(settings.getvCamera().getPosition());

                // Calculate surface normal projection onto the camera
                if (normal.dotProduct(vCameraRay) < 0.0)
                {
                    // Directional lighting specifying the direction from which the light should project
                    Vector3D light_direction = new Vector3D(0, 0, -1);
                    light_direction.normalizeVector();

                    double dp = Math.max(0.1, light_direction.dotProduct(normal));

                    // WORLD SPACE TO VIEW SPACE
                    rasterAssembler.assembleViewMatrix(triViewed, triTrans, matView);

                    // PROJECT 3D GEOMETRICAL DATA TO NORMALIZED 2D SCREEN
                    rasterAssembler.assembleProjectionMatrix(triProjection, triViewed, settings.getMatProj());

                    // CLIP TRIANGLE AGAINST NEAR PLANE
                    int clippedTriangles;
                    Triangle[] clipped = getNearestPlane();
                    clippedTriangles = triViewed.triangleClipAgainstPlane(new Vector3D(0, 0, 0.1d), new Vector3D(0, 0, 1), clipped);

                    for (int n = 0; n < clippedTriangles; n++)
                    {
                        // Project 3D geometrical data to normalized 2D screen
                        rasterAssembler.assembleClipSpace(triProjection, clipped, n, settings.getMatProj());

                        // Scale texture coordinates
                        rasterAssembler.assemblePerspectiveDivide(triProjection);

                        // Scale into view
                        rasterAssembler.scaleIntoView(triProjection);

                        // Set the current lighting color
                        int value = (int) Math.abs(dp * 255);
                        triProjection.color(value, value, value);
                        triProjection.tex = tri.tex;
                        triProjection.dp = dp;

                        vecTrianglesToRaster.add(triProjection);
                    }
                }
            }
        }

        return vecTrianglesToRaster;
    }
}
