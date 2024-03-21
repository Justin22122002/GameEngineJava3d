package org.test.math.vector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2DTest
{
    @Test
    void scale()
    {
        // Create a vector
        Vector2D vector = new Vector2D(2, 3);

        // Test scaling by multiplication
        vector.scale(2, true);
        assertEquals(4, vector.u);
        assertEquals(6, vector.v);

        // Test scaling by division
        vector.scale(2, false);
        assertEquals(2, vector.u);
        assertEquals(3, vector.v);
    }

    @Test
    void testClone()
    {
        // Create a vector
        Vector2D original = new Vector2D(2, 3);

        // Test cloning
        Vector2D cloned = original.clone();
        assertNotSame(original, cloned); // Ensure different objects
        assertEquals(original.u, cloned.u); // Ensure same values
        assertEquals(original.v, cloned.v);
        assertEquals(original.w, cloned.w);
    }
}