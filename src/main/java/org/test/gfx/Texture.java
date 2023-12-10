package org.test.gfx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Objects;

public class Texture
{
    private final int width;
    private final int height;
    private int widthMask;
    private int heightMask;
    private int widthShift;
    private int[] texArray;

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

        if(!isPowerOfTwo(img.getWidth()) || !isPowerOfTwo(img.getHeight()))
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

    public static boolean isPowerOfTwo(int n)
    {
        return ((n & (n-1)) == 0);
    }

    /**
     Counts the number of "on" bits in an integer.
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

    public int getPixel(int x, int y)
    {
        return texArray[x + y * getWidth()];
    }

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
