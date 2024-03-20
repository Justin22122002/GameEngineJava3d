package org.test.gfx;

/**
 * The ZBuffer class represents a depth buffer used in computer graphics to
 * store depth values for each pixel on the screen.
 */
public class ZBuffer
{
    private final int width;
    private final int height;
    private final double[] zBuffer;

    /**
     * Constructs a new ZBuffer with the specified width and height.
     *
     * @param width  The width of the ZBuffer.
     * @param height The height of the ZBuffer.
     */
    public ZBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;
        zBuffer = new double[width * height];
    }

    /**
     * Resets the depth buffer, setting all depth values to zero.
     */
    public void resetBuffer()
    {
        for (int x = 0; x < width * height; x++)
        {
            zBuffer[x] = 0.0;
        }
    }

    /**
     * Sets the depth value at the specified coordinates (x, y) in the ZBuffer.
     *
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param value The depth value to set.
     */
    public void setDepthValue(int x, int y, double value)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
        {
            return;
        }
        zBuffer[x + y * width] = value;
    }

    /**
     * Checks if the depth value at the specified coordinates (x, y) is less than
     * the given value.
     *
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param value The value to compare against.
     * @return True if the depth value is less than the given value, false otherwise.
     */
    public boolean checkDepth(int x, int y, double value)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
        {
            return false;
        }
        return zBuffer[x + y * width] < value;
    }

    /**
     * Retrieves the depth buffer array.
     *
     * @return The depth buffer array.
     */
    public double[] getZBuffer()
    {
        return zBuffer;
    }

    /**
     * Retrieves the width of the ZBuffer.
     *
     * @return The width of the ZBuffer.
     */
    public int getWidth()
    {
        return width;
    }
}
