package org.test.renderer;

import org.test.math.triangle.DrawMode;
import org.test.math.triangle.Mesh;
import org.test.renderdata.RenderSettings;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.test.graphics.ColorUtils.CD_BLACK;
import static org.test.graphics.ColorUtils.CD_WHITE;
import static org.test.graphics.DrawUtils.*;
import static org.test.math.triangle.Triangle.getNearestPlane;
import static org.test.renderer.PanelConfig.getImageHeight;
import static org.test.renderer.PanelConfig.getImageWidth;

/**
 * The Renderer class is responsible for rendering triangles onto a graphics context.
 */
public class Renderer
{
    private final RenderSettings settings = RenderSettings.getInstance();

    /**
     * Renders the provided list of triangles onto the specified graphics context.
     *
     * @param vecTrianglesToRasterMap A map of triangles to be rasterized, with the content, if the mesh is textured or not.
     */
    public void render(Map<List<Triangle>, DrawMode> vecTrianglesToRasterMap)
    {
        clearScreen();
        resetZBuffer();

        // print to screen
        for (List<Triangle> triList : vecTrianglesToRasterMap.keySet())
        {
            for (Triangle t : triList)
            {
                Triangle[] clipped;
                LinkedList<Triangle> listTriangles = new LinkedList<>();
                listTriangles.add(t);
                int nNewTriangles = 1;

                for (int plane = 0; plane < 4; plane++)
                {
                    int trisToAdd;

                    while (nNewTriangles > 0)
                    {
                        clipped = getNearestPlane();

                        Triangle test = listTriangles.peek();
                        listTriangles.pollFirst();
                        nNewTriangles--;

                        trisToAdd = getClippedTriangles(test, clipped, plane);

                        listTriangles.addAll(Arrays.asList(clipped).subList(0, trisToAdd));
                    }
                    nNewTriangles = listTriangles.size();
                }

                for (Triangle triangle : listTriangles)
                {
                    textureTriangle(triangle, vecTrianglesToRasterMap.get(triList));
                }
            }
        }

        updateImage();
    }

    /**
     * Clips the given triangle against the specified plane and stores the result in the provided array.
     *
     * @param test    The triangle to clip.
     * @param clipped The array to store the clipped triangles.
     * @param plane   The index of the plane to clip against. 0 for the bottom, 1 for the top, 2 for the left, 3 for the right.
     * @return The number of triangles resulting from the clipping operation.
     * @throws IllegalArgumentException If the plane index is invalid.
     */
    private int getClippedTriangles(Triangle test, Triangle[] clipped, int plane)
    {
        switch (plane)
        {
            case 0 ->
            {
                return test.triangleClipAgainstPlane(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), clipped);
            }
            case 1 ->
            {
                return test.triangleClipAgainstPlane(new Vector3D(0, getImageHeight() - 1.0, 0), new Vector3D(0, -1, 0), clipped);
            }
            case 2 ->
            {
                return test.triangleClipAgainstPlane(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), clipped);
            }
            case 3 ->
            {
                return test.triangleClipAgainstPlane(new Vector3D(getImageWidth() - 1.0, 0, 0), new Vector3D(-1, 0, 0), clipped);
            }
            default -> throw new IllegalArgumentException(STR."Invalid value for plane: \{plane}");
        }
    }

    /**
     * Renders a textured triangle onto the screen.
     *
     * @param triangle The triangle to render.
     * @param drawMode option for rendering the triangle
     */
    private void textureTriangle(Triangle triangle, DrawMode drawMode)
    {
        switch (drawMode)
        {
            case DrawMode.TEXTURED -> TexturedTriangle((int) triangle.vec3D.x, (int) triangle.vec3D.y, triangle.vec2D.u, triangle.vec2D.v, triangle.vec2D.w,
                (int) triangle.vec3D2.x, (int) triangle.vec3D2.y, triangle.vec2D2.u, triangle.vec2D2.v, triangle.vec2D2.w,
                (int) triangle.vec3D3.x, (int) triangle.vec3D3.y, triangle.vec2D3.u, triangle.vec2D3.v, triangle.vec2D3.w,
                triangle.tex, 0, false, false, settings.getPixels(), settings.getzBuffer(), triangle.dp);
            case DrawMode.WIREFRAME -> slDrawTriangle(settings.getPixels(), (int) triangle.vec3D.x, (int) triangle.vec3D.y, (int) triangle.vec3D2.x, (int) triangle.vec3D2.y,
                    (int) triangle.vec3D3.x, (int) triangle.vec3D3.y, CD_WHITE);
            case DrawMode.SURFACE -> slFillTriangle(settings.getPixels(),(int)triangle.vec3D.x,(int)triangle.vec3D.y,(int)triangle.vec3D2.x,(int)triangle.vec3D2.y,
                    (int)triangle.vec3D3.x,(int)triangle.vec3D3.y,triangle.color.getRGB(), settings.getzBuffer());
        }
    }

    /**
     * Clears the screen with a black color.
     */
    private void clearScreen()
    {
        slFill(settings.getPixels(), CD_BLACK);
    }

    /**
     * Resets the Z-buffer.
     */
    private void resetZBuffer()
    {
        settings.getzBuffer().resetBuffer();
    }

    /**
     * Updates the image after rendering.
     */
    private void updateImage()
    {
        settings.getmImageProducer().newPixels();
    }
}
