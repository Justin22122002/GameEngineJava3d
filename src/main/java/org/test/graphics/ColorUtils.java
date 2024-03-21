package org.test.graphics;

/**
 * Utility class for manipulating color values in Java.
 */
public class ColorUtils
{
    // Color constants
    public static final int CD_BLACK = 0x000000;
    public static final int CD_WHITE = 0xffffff;
    public static final int CD_RED = 0xff0000;
    public static final int CD_GREEN = 0x00ff00;
    public static final int CD_BLUE = 0x0000ff;
    public static final int CD_ORANGE = 0xFFA500;
    public static final int CD_GRAY = 0xFF808080;
    public static final int CD_YELLOW = -256;

    /**
     * Mixes two colors together by adding their components.
     *
     * @param col  The first color.
     * @param col2 The second color.
     * @return The mixed color.
     */
    public static int mix(int col, int col2)
    {
        int[] rgba1 = getColorComponents(col);
        int[] rgba2 = getColorComponents(col2);

        int a = rgba1[0] + rgba2[0];
        int r = rgba1[1] + rgba2[1];
        int g = rgba1[2] + rgba2[2];
        int b = rgba1[3] + rgba2[3];

        return assembleColor(a, r, g, b);
    }

    /**
     * Multiplies two colors together by multiplying their components.
     *
     * @param col  The first color.
     * @param col2 The second color.
     * @return The multiplied color.
     */
    public static int multiply(int col, int col2)
    {
        int[] rgba1 = getColorComponents(col);
        int[] rgba2 = getColorComponents(col2);

        int a = rgba1[0] * rgba2[0];
        int r = rgba1[1] * rgba2[1];
        int g = rgba1[2] * rgba2[2];
        int b = rgba1[3] * rgba2[3];

        return assembleColor(a, r, g, b);
    }

    /**
     * Blends two colors together using a specified ratio.
     *
     * @param col   The first color.
     * @param col2  The second color.
     * @param ratio The blending ratio (0.0 to 1.0).
     * @return The blended color.
     */
    public static int blend(int col, int col2, double ratio)
    {
        if (ratio > 1.0) ratio = 1.0;
        else if (ratio < 0.0) ratio = 0.0;
        double iRatio = 1.0 - ratio;

        int[] rgba1 = getColorComponents(col);
        int[] rgba2 = getColorComponents(col2);

        int a = (int) ((rgba1[0] * iRatio) + (rgba2[0] * ratio));
        int r = (int) ((rgba1[1] * iRatio) + (rgba2[1] * ratio));
        int g = (int) ((rgba1[2] * iRatio) + (rgba2[2] * ratio));
        int b = (int) ((rgba1[3] * iRatio) + (rgba2[3] * ratio));

        return assembleColor(a, r, g, b);
    }

    /**
     * Adjusts the color intensity by scaling each component with a given factor.
     *
     * @param col    The color.
     * @param factor The scaling factor.
     * @return The adjusted color.
     */
    public static int dotColor(int col, double factor)
    {
        int[] rgba = getColorComponents(col);

        int a = (int) (rgba[0] * factor);
        int r = (int) (rgba[1] * factor);
        int g = (int) (rgba[2] * factor);
        int b = (int) (rgba[3] * factor);

        return assembleColor(a, r, g, b);
    }

    /**
     * Extracts the alpha value (transparency) from an ARGB color value.
     *
     * @param col The ARGB color value from which the alpha value is to be extracted.
     * @return The alpha value of the color in the range of 0 to 255.
     */
    public static int getA(int col)
    {
        return (col >> 24 & 0xff);
    }

    /**
     * Extracts the red value from an ARGB color value.
     *
     * @param col The ARGB color value from which the red value is to be extracted.
     * @return The red value of the color in the range of 0 to 255.
     */
    public static int getR(int col)
    {
        return ((col & 0xff0000) >> 16);
    }

    /**
     * Extracts the green value from an ARGB color value.
     *
     * @param col The ARGB color value from which the green value is to be extracted.
     * @return The green value of the color in the range of 0 to 255.
     */
    public static int getG(int col)
    {
        return ((col & 0xff00) >> 8);
    }

    /**
     * Extracts the blue value from an ARGB color value.
     *
     * @param col The ARGB color value from which the blue value is to be extracted.
     * @return The blue value of the color in the range of 0 to 255.
     */
    public static int getB(int col)
    {
        return (col & 0xff);
    }
    /**
     * Assembles an ARGB color value from individual color components.
     *
     * @param a The alpha component (transparency).
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return The assembled ARGB color value.
     */
    private static int assembleColor(int a, int r, int g, int b)
    {
        return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    /**
     * Extracts the individual color components (ARGB) from a color value.
     *
     * @param col The ARGB color value.
     * @return An array containing the alpha, red, green, and blue components.
     */
    private static int[] getColorComponents(int col)
    {
        int[] components = new int[4];
        components[0] = (col >> 24) & 0xff; // Alpha
        components[1] = (col >> 16) & 0xff; // Red
        components[2] = (col >> 8) & 0xff; // Green
        components[3] = col & 0xff; // Blue
        return components;
    }
}
