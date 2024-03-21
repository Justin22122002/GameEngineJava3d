package org.test.math.triangle;

import org.junit.jupiter.api.Test;
import org.test.math.vector.Vector2D;
import org.test.math.vector.Vector3D;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTest
{
    @Test
    void color()
    {
        Triangle triangle = new Triangle();
        triangle.color(255, 0, 0); // Set color to red
        assertEquals(triangle.color.getRed(), 255);
        assertEquals(triangle.color.getGreen(), 0);
        assertEquals(triangle.color.getBlue(), 0);
    }

    @Test
    void triangleClipAgainstPlane()
    {
        // Define test data
        Vector3D plane_p = new Vector3D(0, 0, 0);
        Vector3D plane_n = new Vector3D(0, 0, 1); // Normal vector pointing upwards
        Triangle triangle = new Triangle
                (
                        new Vector3D(0, 0, 0),
                        new Vector3D(1, 0, 0),
                        new Vector3D(0, 1, 0),
                        new Vector2D(0, 0),
                        new Vector2D(1, 0),
                        new Vector2D(0, 1)
                );
        Triangle[] out = new Triangle[2]; // Array to store output triangles

        // Perform triangle clipping
        int numTriangles = triangle.triangleClipAgainstPlane(plane_p, plane_n, out);

        // Validate results
        assertEquals(numTriangles, 1); // Only one triangle should be returned
        assertNotNull(out[0]);
        // Additional assertions can be added based on specific requirements
        // For example, if you expect specific vertices or texture coordinates after clipping:
        assertEquals(out[0].vec3D, new Vector3D(0, 0, 0)); // Check the position of the first vertex
        assertEquals(out[0].vec2D, new Vector2D(0, 0)); // Check the texture coordinates of the first vertex
    }
}