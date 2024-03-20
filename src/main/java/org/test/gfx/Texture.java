package org.test.gfx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Objects;

/**
 * Represents a texture used in graphics rendering.
 */
public class Texture
{
    private final int width;
    private final int height;
    private int widthMask;
    private int heightMask;
    private int widthShift;
    private int[] texArray;

    /**
     * Constructs a texture from a BufferedImage.
     *
     * @param img The BufferedImage representing the texture.
     */
    public Texture(BufferedImage img)
    {
        this.width = img.getWidth();
        this.height = img.getHeight();

        if(img.getType() != BufferedImage.TYPE_INT_RGB)
        {
            BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            img = newImage;
        }

        texArray = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        widthMask = img.getWidth() - 1;
        heightMask = img.getHeight() - 1;
        widthShift = countBits(getWidth()-1);
    }

    /**
     * Creates a texture from an image file.
     *
     * @param fileName The path to the image file.
     */
    public void createTexture(String fileName)
    {
        BufferedImage img = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        try
        {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource(fileName)));

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(isPowerOfTwo(img.getWidth()) || isPowerOfTwo(img.getHeight()))
        {
            throw new IllegalArgumentException("Current texture width or height is not a power of 2 which slows down performance. Resize to power of two");
        }

        if (img.getType() != BufferedImage.TYPE_INT_RGB)
        {
            BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            img = newImage;
        }
        texArray = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        widthMask = img.getWidth() - 1;
        heightMask = img.getHeight() - 1;
        widthShift = countBits(getWidth()-1);
    }

    /**
     * Checks if a given number is a power of two.
     *
     * @param n The number to check.
     * @return True if the number is a power of two, otherwise false.
     */
    public static boolean isPowerOfTwo(int n)
    {
        return ((n & (n - 1)) != 0);
    }

    /**
     * Counts the number of set bits in an integer.
     *
     * @param n The integer to count bits.
     * @return The number of set bits.
     */
    public static int countBits(int n)
    {
        int count = 0;
        while (n > 0) {
            count+=(n & 1);
            n>>=1;
        }
        return count;
    }

    /**
     * Gets the pixel color at the specified coordinates.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return The color of the pixel.
     */
    public int getPixel(int x, int y)
    {
        return texArray[x + y * getWidth()];
    }

    /**
     * Sets the color of the pixel at the specified coordinates.
     *
     * @param x   The x-coordinate of the pixel.
     * @param y   The y-coordinate of the pixel.
     * @param col The color to set.
     * @return The updated color of the pixel.
     */
    public int setPixel(int x, int y, int col)
    {
        return texArray[x + y * getWidth()] = col;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidthMask()
    {
        return widthMask;
    }

    public void setWidthMask(int widthMask)
    {
        this.widthMask = widthMask;
    }

    public int getHeightMask()
    {
        return heightMask;
    }

    public void setHeightMask(int heightMask)
    {
        this.heightMask = heightMask;
    }

    public int getWidthShift()
    {
        return widthShift;
    }

    public void setWidthShift(int widthShift)
    {
        this.widthShift = widthShift;
    }

    public int[] getTexArray()
    {
        return texArray;
    }

    public void setTexArray(int[] texArray)
    {
        this.texArray = texArray;
    }
}
