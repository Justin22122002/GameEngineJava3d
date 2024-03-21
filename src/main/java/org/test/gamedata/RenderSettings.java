package org.test.gamedata;

import org.test.input.KeyHandler;
import org.test.graphics.ZBuffer;
import org.test.math.matrix.Matrix4x4;
import org.test.math.triangle.PolygonGroup;
import org.test.math.vector.Vector3D;
import org.test.renderer.Camera;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

import static org.test.Main.getImageHeight;
import static org.test.Main.getImageWidth;

public class RenderSettings
{
    private static RenderSettings instance;
    Thread gameThread;

    // Rendering Information
    private Image imageBuffer;
    private MemoryImageSource mImageProducer;
    private int[] pixels;
    private final ZBuffer zBuffer;

    private KeyHandler keyH;

    // Frames per second
    private double fps = 60;

    // Projection matrix
    private Matrix4x4 matProj; // projection matrix
    private Matrix4x4 matZ, matZX; // rotation matrix
    private PolygonGroup polygonGroup; // stores all Objects
    private Camera vCamera; // stationary position vector, that will serve as the camera

    public RenderSettings()
    {
        vCamera = new Camera(0, 0, 0); // default Camera
        matProj = Matrix4x4.projektionMatrix(vCamera.getfNear(), vCamera.getfFar(), vCamera.getA(), vCamera.getFov());
        zBuffer = new ZBuffer((int) getImageWidth(), (int) getImageHeight());
        keyH = new KeyHandler();

        this.setUpImageProducer();
    }

    private void setUpImageProducer()
    {
        ColorModel cm = getCompatibleColorModel();

        // calculate the pixels depending on the current screen / Panel size
        int screenSize = (int) getImageWidth() * (int) getImageHeight();
        if(pixels == null || pixels.length < screenSize)
        {
            pixels = new int[screenSize];
        }

        // This class is an implementation of the ImageProducer interface which uses an array
        // to produce pixel values for an Image.
        mImageProducer = new MemoryImageSource((int) getImageWidth(), (int) getImageHeight(), cm, pixels,0, (int) getImageWidth());
        mImageProducer.setAnimated(true);
        mImageProducer.setFullBufferUpdates(true);
        imageBuffer = Toolkit.getDefaultToolkit().createImage(mImageProducer);
    }

    public static RenderSettings getInstance()
    {
        if (instance == null)
        {
            instance = new RenderSettings();
        }
        return instance;
    }

    protected static ColorModel getCompatibleColorModel()
    {
        GraphicsConfiguration gfx_config = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        return gfx_config.getColorModel();
    }

    public Thread getGameThread()
    {
        return gameThread;
    }

    public void setGameThread(Thread gameThread)
    {
        this.gameThread = gameThread;
    }

    public Image getImageBuffer()
    {
        return imageBuffer;
    }

    public void setImageBuffer(Image imageBuffer)
    {
        this.imageBuffer = imageBuffer;
    }

    public MemoryImageSource getmImageProducer()
    {
        return mImageProducer;
    }

    public void setmImageProducer(MemoryImageSource mImageProducer)
    {
        this.mImageProducer = mImageProducer;
    }

    public int[] getPixels()
    {
        return pixels;
    }

    public void setPixels(int[] pixels)
    {
        this.pixels = pixels;
    }

    public ZBuffer getzBuffer()
    {
        return zBuffer;
    }

    public KeyHandler getKeyH()
    {
        return keyH;
    }

    public void setKeyH(KeyHandler keyH)
    {
        this.keyH = keyH;
    }

    public double getFps()
    {
        return fps;
    }

    public void setFps(double fps)
    {
        this.fps = fps;
    }

    public Matrix4x4 getMatProj()
    {
        return matProj;
    }

    public void setMatProj(Matrix4x4 matProj)
    {
        this.matProj = matProj;
    }

    public Matrix4x4 getMatZ()
    {
        return matZ;
    }

    public void setMatZ(Matrix4x4 matZ)
    {
        this.matZ = matZ;
    }

    public Matrix4x4 getMatZX()
    {
        return matZX;
    }

    public void setMatZX(Matrix4x4 matZX)
    {
        this.matZX = matZX;
    }

    public PolygonGroup getPolygonGroup()
    {
        return polygonGroup;
    }

    public void setPolygonGroup(PolygonGroup polygonGroup)
    {
        this.polygonGroup = polygonGroup;
    }

    public Camera getvCamera()
    {
        return vCamera;
    }

    public void setvCamera(Camera vCamera)
    {
        this.vCamera = vCamera;
    }
}
