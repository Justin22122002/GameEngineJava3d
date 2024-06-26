package org.test.graphics;

import static org.test.renderer.PanelConfig.getImageHeight;
import static org.test.renderer.PanelConfig.getImageWidth;
import static org.test.graphics.ColorUtils.CD_GRAY;
import static org.test.graphics.ColorUtils.CD_WHITE;
import static org.test.graphics.ColorUtils.blend;
import static org.test.graphics.ColorUtils.dotColor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class DrawUtils
{
    public static void draw(int[] pixels, int x, int y, int col)
    {
        if (x >= 0 && y >= 0 && x < getImageWidth() - 1 && y < getImageHeight() - 1)
        {
            pixels[x + y * (int) getImageWidth()] = col;
        }
    }

    public static void slDrawTriangle(int[] pixels, int x1, int y1, int x2, int y2, int x3, int y3, int col)
    {
        slDrawLine(pixels, x1, y1, x2, y2, col);
        slDrawLine(pixels, x2, y2, x3, y3, col);
        slDrawLine(pixels, x3, y3, x1, y1, col);
    }

    public static void slDrawLine(int[] pixels, int x0, int y0, int x1, int y1, int color)
    {
        int dy = y1 - y0;
        int dx = x1 - x0;
        int sx, sy;

        if (dy < 0)
        {
            dy = -dy;
            sy = -1;
        } else
            sy = 1;

        if (dx < 0)
        {
            dx = -dx;
            sx = -1;
        } else
            sx = 1;

        dy <<= 1;
        dx <<= 1;

        draw(pixels, x0, y0, color);
        if (dx > dy)
        {
            int frac = dy - (dx >> 1);
            while (x0 != x1)
            {
                if (frac >= 0)
                {
                    y0 += sy;
                    frac -= dx;
                }
                x0 += sx;
                frac += dy;
                draw(pixels, x0, y0, color);
            }
        } else
        {
            int frac = dx - (dy >> 1);
            while (y0 != y1)
            {
                if (frac >= 0)
                {
                    x0 += sx;
                    frac -= dy;
                }
                y0 += sy;
                frac += dx;
                draw(pixels, x0, y0, color);
            }
        }
    }

    public static void slFill(int[] pixels, int col)
    {
        for (int y = 0; y < getImageHeight(); y++)
        {
            for (int x = 0; x < getImageWidth(); x++)
            {
                pixels[x + y * (int) getImageWidth()] = col;
            }
        }
    }

    public static void slFillRect(int[] pixels, int x, int y, int width, int height, int col)
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                draw(pixels, i + x, j + y, col);
            }
        }
    }

    public static void slFillTriangle(int[] canvas, int x1, int y1, int x2, int y2, int x3, int y3, int col, ZBuffer zBuffer)
    {
        int t1x = 0, t2x = 0, y = 0, minx = 0, maxx = 0, t1xp = 0, t2xp = 0;
        boolean changed1 = false;
        boolean changed2 = false;
        int signx1 = 0, signx2 = 0, dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        int e1 = 0, e2 = 0;
        // Sort vertices
        if (y1 > y2)
        {
            int t = y1;
            y1 = y2;
            y2 = t;
            t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y1 > y3)
        {
            int t = y1;
            y1 = y3;
            y3 = t;
            t = x1;
            x1 = x3;
            x3 = t;
        }
        if (y2 > y3)
        {
            int t = y2;
            y2 = y3;
            y3 = t;
            t = x2;
            x2 = x3;
            x3 = t;
        }

        t1x = t2x = x1;
        y = y1;   // Starting points
        dx1 = (x2 - x1);
        if (dx1 < 0)
        {
            dx1 = -dx1;
            signx1 = -1;
        } else signx1 = 1;
        dy1 = y2 - y1;

        dx2 = x3 - x1;
        if (dx2 < 0)
        {
            dx2 = -dx2;
            signx2 = -1;
        } else signx2 = 1;
        dy2 = y3 - y1;

        if (dy1 > dx1)
        {   // swap values
            int t = dx1;
            dx1 = dy1;
            dy1 = t;
            changed1 = true;
        }
        if (dy2 > dx2)
        {   // swap values
            int t = dy2;
            dy2 = dx2;
            dx2 = t;
            changed2 = true;
        }

        e2 = dx2 >> 1;
        // Flat top, just process the second half
        boolean goNext = y1 == y2;
        if (!goNext)
        {
            e1 = dx1 >> 1;

            for (int i = 0; i < dx1; )
            {
                t1xp = 0;
                t2xp = 0;
                if (t1x < t2x)
                {
                    minx = t1x;
                    maxx = t2x;
                } else
                {
                    minx = t2x;
                    maxx = t1x;
                }
                // process first line until y value is about to change
                loop3:
                while (i < dx1)
                {
                    i++;
                    e1 += dy1;
                    while (e1 >= dx1)
                    {
                        e1 -= dx1;
                        if (changed1) t1xp = signx1;
                        else break loop3;
                    }
                    if (changed1) break;
                    else t1x += signx1;
                }
                // Move line
                // process second line until y value is about to change
                loop2:
                while (true)
                {
                    e2 += dy2;
                    while (e2 >= dx2)
                    {
                        e2 -= dx2;
                        if (changed2) t2xp = signx2;
                        else break loop2;
                    }
                    if (changed2) break;
                    else t2x += signx2;
                }
                if (minx > t1x) minx = t1x;
                if (minx > t2x) minx = t2x;
                if (maxx < t1x) maxx = t1x;
                if (maxx < t2x) maxx = t2x;
                drawLine(canvas, minx, maxx, y, col);    // Draw line from min to max points found on the y
                // Now increase y
                if (!changed1) t1x += signx1;
                t1x += t1xp;
                if (!changed2) t2x += signx2;
                t2x += t2xp;
                y += 1;
                if (y == y2) break;

            }
        }
        // Second half
        dx1 = x3 - x2;
        if (dx1 < 0)
        {
            dx1 = -dx1;
            signx1 = -1;
        } else signx1 = 1;
        dy1 = y3 - y2;
        t1x = x2;

        if (dy1 > dx1)
        {   // swap values
            int t = dy1;
            dy1 = dx1;
            dx1 = t;
            changed1 = true;
        } else changed1 = false;

        e1 = dx1 >> 1;

        for (int i = 0; i <= dx1; i++)
        {
            t1xp = 0;
            t2xp = 0;
            if (t1x < t2x)
            {
                minx = t1x;
                maxx = t2x;
            } else
            {
                minx = t2x;
                maxx = t1x;
            }
            // process first line until y value is about to change
            loop3:
            while (i < dx1)
            {
                e1 += dy1;
                while (e1 >= dx1)
                {
                    e1 -= dx1;
                    if (changed1)
                    {
                        t1xp = signx1;
                        break;
                    }
                    else break loop3;
                }
                if (changed1) break;
                else t1x += signx1;
                i++;
            }
            // process second line until y value is about to change
            loop2:
            while (t2x != x3)
            {
                e2 += dy2;
                while (e2 >= dx2)
                {
                    e2 -= dx2;
                    if (changed2) t2xp = signx2;
                    else break loop2;
                }
                if (changed2) break;
                else t2x += signx2;
            }
            if (minx > t1x) minx = t1x;
            if (minx > t2x) minx = t2x;
            if (maxx < t1x) maxx = t1x;
            if (maxx < t2x) maxx = t2x;
            drawLine(canvas, minx, maxx, y, col);
            if (!changed1) t1x += signx1;
            t1x += t1xp;
            if (!changed2) t2x += signx2;
            t2x += t2xp;
            y += 1;
            if (y > y3) return;
        }
    }

    public static void drawLine(int[] pixels, int sx, int ex, int ny, int col)
    {
        for (int i = sx; i <= ex; i++)
        {
            draw(pixels, i, ny, col);
        }
    }

    public static void TexturedTriangle
            (
                    int x1, int y1,
                    double u1, double v1, double w1,
                    int x2, int y2,
                    double u2, double v2, double w2,
                    int x3, int y3,
                    double u3, double v3, double w3,
                    Texture tex,
                    double visibility,
                    boolean fog,
                    boolean directionLighting,
                    int[] pix, ZBuffer zBuffer,
                    double dp)
    {
        if (y2 < y1)
        {
            int temp = y1;
            y1 = y2;
            y2 = temp;

            int tempx = x1;
            x1 = x2;
            x2 = tempx;

            double tempu = u1;
            u1 = u2;
            u2 = tempu;

            double tempv = v1;
            v1 = v2;
            v2 = tempv;

            double tempw = w1;
            w1 = w2;
            w2 = tempw;

        }

        if (y3 < y1)
        {
            int temp = y1;
            y1 = y3;
            y3 = temp;

            int tempx = x1;
            x1 = x3;
            x3 = tempx;

            double tempu = u1;
            u1 = u3;
            u3 = tempu;

            double tempv = v1;
            v1 = v3;
            v3 = tempv;

            double tempw = w1;
            w1 = w3;
            w3 = tempw;
        }

        if (y3 < y2)
        {
            int temp = y2;
            y2 = y3;
            y3 = temp;

            int tempx = x2;
            x2 = x3;
            x3 = tempx;

            double tempu = u2;
            u2 = u3;
            u3 = tempu;

            double tempv = v2;
            v2 = v3;
            v3 = tempv;

            double tempw = w2;
            w2 = w3;
            w3 = tempw;

        }

        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        double dv1 = v2 - v1;
        double du1 = u2 - u1;
        double dw1 = w2 - w1;

        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        double dv2 = v3 - v1;
        double du2 = u3 - u1;
        double dw2 = w3 - w1;

        double tex_u, tex_v, tex_w;

        double dax_step = 0, dbx_step = 0, du1_step = 0, dv1_step = 0, du2_step = 0, dv2_step = 0, dw1_step = 0, dw2_step = 0;

        if (dy1 != 0) dax_step = dx1 / (double) Math.abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (double) Math.abs(dy2);

        if (dy1 != 0) du1_step = du1 / (double) Math.abs(dy1);
        if (dy1 != 0) dv1_step = dv1 / (double) Math.abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (double) Math.abs(dy1);

        if (dy2 != 0) du2_step = du2 / (double) Math.abs(dy2);
        if (dy2 != 0) dv2_step = dv2 / (double) Math.abs(dy2);
        if (dy2 != 0) dw2_step = dw2 / (double) Math.abs(dy2);

        if (dy1 != 0)
        {
            for (int i = y1; i <= y2; i++)
            {
                int ax = (int) (x1 + (i - y1) * dax_step);
                int bx = (int) (x1 + (i - y1) * dbx_step);

                double tex_su = u1 + (double) (i - y1) * du1_step;
                double tex_sv = v1 + (double) (i - y1) * dv1_step;
                double tex_sw = w1 + (double) (i - y1) * dw1_step;

                double tex_eu = u1 + (double) (i - y1) * du2_step;
                double tex_ev = v1 + (double) (i - y1) * dv2_step;
                double tex_ew = w1 + (double) (i - y1) * dw2_step;

                if (ax > bx)
                {
                    int temp = ax;
                    ax = bx;
                    bx = temp;

                    double temps = tex_su;
                    tex_su = tex_eu;
                    tex_eu = temps;

                    double tempv = tex_sv;
                    tex_sv = tex_ev;
                    tex_ev = tempv;

                    double tempw = tex_sw;
                    tex_sw = tex_ew;
                    tex_ew = tempw;
                }

                double tstep = 1.0 / (double) (bx - ax);
                double t = 0.0;

                for (int j = ax; j < bx; j++)
                {
                    tex_u = (1.0 - t) * tex_su + t * tex_eu;
                    tex_v = (1.0 - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0 - t) * tex_sw + t * tex_ew;

                    if (zBuffer.checkDepth(j, i, Math.abs(tex_w)))
                    {
                        int iu = (int) ((tex_u / tex_w) * tex.getWidth()) & tex.getWidthMask();
                        int iv = (int) ((tex_v / tex_w) * tex.getHeight()) & tex.getHeightMask();
                        int col = tex.getTexArray()[iu + (iv << tex.getWidthShift())];

                        int backgroundColor = blend(CD_GRAY, CD_WHITE, 0.4);

                        if (fog)
                        {
                            col = blend(backgroundColor, col, visibility);
                        }

                        if (directionLighting)
                        {
                            col = dotColor(col, dp);
                        }

                        draw(pix, j, i, col);
                        zBuffer.setDepthValue(j, i, Math.abs(tex_w));
                    }

                    t += tstep;
                }

            }

        }

        dy1 = y3 - y2;
        dx1 = x3 - x2;
        dv1 = v3 - v2;
        du1 = u3 - u2;
        dw1 = w3 - w2;

        if (dy1 != 0) dax_step = dx1 / (double) Math.abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (double) Math.abs(dy2);

        du1_step = 0;
        dv1_step = 0;
        dw1_step = 0;

        if (dy1 != 0) du1_step = du1 / (double) Math.abs(dy1);
        if (dy1 != 0) dv1_step = dv1 / (double) Math.abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (double) Math.abs(dy1);

        if (dy1 != 0)
        {
            for (int i = y2; i <= y3; i++)
            {
                int ax = (int) (x2 + (double) (i - y2) * dax_step);
                int bx = (int) (x1 + (double) (i - y1) * dbx_step);

                double tex_su = u2 + (double) (i - y2) * du1_step;
                double tex_sv = v2 + (double) (i - y2) * dv1_step;
                double tex_sw = w2 + (double) (i - y2) * dw1_step;

                double tex_eu = u1 + (double) (i - y1) * du2_step;
                double tex_ev = v1 + (double) (i - y1) * dv2_step;
                double tex_ew = w1 + (double) (i - y1) * dw2_step;

                if (ax > bx)
                {
                    int temp = ax;
                    ax = bx;
                    bx = temp;

                    double temps = tex_su;
                    tex_su = tex_eu;
                    tex_eu = temps;

                    double tempv = tex_sv;
                    tex_sv = tex_ev;
                    tex_ev = tempv;

                    double tempw = tex_sw;
                    tex_sw = tex_ew;
                    tex_ew = tempw;
                }

                double tstep = 1.0 / (double) (bx - ax);
                double t = 0.0;

                for (int j = ax; j < bx; j++)
                {
                    tex_u = (1.0 - t) * tex_su + t * tex_eu;
                    tex_v = (1.0 - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0 - t) * tex_sw + t * tex_ew;

                    if (zBuffer.checkDepth(j, i, Math.abs(tex_w)))
                    {
                        int iu = (int) ((tex_u / tex_w) * tex.getWidth()) & tex.getWidthMask();
                        int iv = (int) ((tex_v / tex_w) * tex.getHeight()) & tex.getHeightMask();
                        int col = tex.getTexArray()[iu + (iv << tex.getWidthShift())];

                        int backgroundColor = blend(CD_GRAY, CD_WHITE, 0.4);

                        if (fog)
                        {
                            col = blend(backgroundColor, col, visibility);
                        }

                        if (directionLighting)
                        {
                            col = dotColor(col, dp);
                        }

                        draw(pix, j, i, col);

                        zBuffer.setDepthValue(j, i, Math.abs(tex_w));
                    }
                    t += tstep;
                }
            }
        }
    }

    public static void slBlendTexture(Texture sprite, int col, double factor)
    {
        for (int y = 0; y < sprite.getHeight(); y++)
        {
            for (int x = 0; x < sprite.getWidth(); x++)
            {
                if (sprite.getPixel(x, y) != 0)
                {
                    sprite.setPixel(x, y, blend(sprite.getPixel(x, y), col, factor));
                }
            }
        }
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bufferedImage;
    }
}
