package org.test.gfx;

public class ZBuffer
{
    private final int width;
    private final int height;
    private final double[] zBuffer;

    public ZBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;

        zBuffer = new double[width * height];
    }

    public void resetBuffer()
    {
        for(int x = 0; x < width * height; x++)
        {
            zBuffer[x] = 0.0;
        }
    }

    public void setDepthValue(int x, int y, double value)
    {
        if(x < 0 || y < 0 || x >= width || y >= height)
        {
            return;
        }

        zBuffer[x + y * width] = value;
    }

    public boolean checkDepth(int x, int y, double value)
    {
        if(x < 0 || y < 0 || x >= width || y >= height)
        {
            return false;
        }

        if(zBuffer[x + y * width] < value)
        {
            return true;
        }
        else return false;
    }

    public double[] getZBuffer()
    {
        return zBuffer;
    }

    public int getWidth()
    {
        return width;
    }
}