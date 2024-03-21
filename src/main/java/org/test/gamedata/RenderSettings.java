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
    private double fNear = 0.1; // nearest clip plane
    private double fFar = 1000.0;  // furthers clip plane
    private double fov = 90.0; // flied of view
    private double a = getImageHeight() / getImageWidth(); // aspect ratio of the window
    private double fYaw = 0.0;
    private double fPitch = 0.0;
    private double fTheta; // angle used in rotation matrix

    // Projection matrix
    private Matrix4x4 matProj; // projection matrix
    private Matrix4x4 matZ, matZX; // rotation matrix
    private PolygonGroup polygonGroup; // stores all Objects
    private Camera vCamera; // stationary position vector, that will serve as the camera
    private Vector3D vLookDir; // camera that follows along the look at direction

    public RenderSettings()
    {
        vCamera = new Camera(0, 0, 0);
        vLookDir = new Vector3D(0, 0, 1);
        matProj = Matrix4x4.projektionMatrix(fNear, fFar, a, fov);
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

    public double getfNear()
    {
        return fNear;
    }

    public void setfNear(double fNear)
    {
        this.fNear = fNear;
    }

    public double getfFar()
    {
        return fFar;
    }

    public void setfFar(double fFar)
    {
        this.fFar = fFar;
    }

    public double getFov()
    {
        return fov;
    }

    public void setFov(double fov)
    {
        this.fov = fov;
    }

    public double getA()
    {
        return a;
    }

    public void setA(double a)
    {
        this.a = a;
    }

    public double getfYaw()
    {
        return fYaw;
    }

    public void setfYaw(double fYaw)
    {
        this.fYaw = fYaw;
    }

    public double getfPitch()
    {
        return fPitch;
    }

    public void setfPitch(double fPitch)
    {
        this.fPitch = fPitch;
    }

    public double getfTheta()
    {
        return fTheta;
    }

    public void setfTheta(double fTheta)
    {
        this.fTheta = fTheta;
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

    public Vector3D getvLookDir()
    {
        return vLookDir;
    }

    public void setvLookDir(Vector3D vLookDir)
    {
        this.vLookDir = vLookDir;
    }
}
