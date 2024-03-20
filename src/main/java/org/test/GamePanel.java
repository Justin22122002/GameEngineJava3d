package org.test;

import org.test.gfx.Texture;
import org.test.gfx.ZBuffer;
import org.test.math.*;
import org.test.renderer.Camera;
import org.test.renderer.RasterAssembler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.test.Main.getImageHeight;
import static org.test.Main.getImageWidth;
import static org.test.gfx.ColorUtils.CD_BLACK;
import static org.test.gfx.DrawUtils.TexturedTriangle;
import static org.test.gfx.DrawUtils.slFill;

public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;
    private final Image imageBuffer;
    private final MemoryImageSource mImageProducer;
    private int[] pixels;
    private final ZBuffer zBuffer = new ZBuffer((int) getImageWidth(), (int) getImageHeight());;

    // Frames per second
    double fps = 60;
    KeyHandler keyH = new KeyHandler();

    // Projection matrix
    double fNear = 0.1; // nearest clip plane
    double fFar = 1000.0;  // furthers clip plane
    double fov = 90.0; // flied of view
    double a = getImageHeight() / getImageWidth(); // aspect ratio of the window
    Matrix mat = new Matrix();
    Matrix matProj = Matrix.projektionMatrix(fNear, fFar, a, fov); // projection matrix
    Matrix matZ, matZX; // rotation matrix
    double fTheta; // angle used in rotation matrix
    Mesh meshCube = new Mesh(); // collection of triangles that form an object
    PolygonGroup polygonGroup;
    Camera vCamera = new Camera(0, 0, 0); // stationary position vector, that will serve as the camera
    Vec3D vLookDir = new Vec3D(0, 0, 1); // camera that follows along the look at direction
    double fYaw = 0.0;
    double fPitch = 0.0;
    boolean drawEdges = true;
    RasterAssembler rasterAssembler = new RasterAssembler();

    public GamePanel()
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

        // Basic JPanel Configuration
        this.addKeyListener(keyH);
        this.addMouseMotionListener(keyH);
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        initializeMesh();
    }

    protected static ColorModel getCompatibleColorModel()
    {
        GraphicsConfiguration gfx_config = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        return gfx_config.getColorModel();
    }

    public void startThread()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run()
    {
        double drawInterval = 1_000_000_000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        int timer = 0;
        int drawCount = 0;

        while (gameThread != null)
        {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (int) (currentTime - lastTime);

            lastTime = currentTime;

            if (delta >= 1)
            {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1_000_000_000)
            {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void initializeMesh()
    {
        polygonGroup = new PolygonGroup();

        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("./car4.png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        meshCube = new Mesh(meshCube.ReadOBJFile("car4.obj", true));

        for(Triangle t : meshCube.triangles)
        {
            assert img != null;
            t.tex = new Texture(img);
        }

        polygonGroup.addMesh(meshCube);
    }

    public void update()
    {
        Vec3D vForward = vLookDir.normalizeVector(); // Normalisierte Blickrichtung

        if (keyH.rightPressed)
        {
            vCamera.setCam(vCamera.getCam().subtractVector(vForward.crossProduct(new Vec3D(0, 0.1, 0))));
        }

        if (keyH.leftPressed)
        {
            vCamera.setCam(vCamera.getCam().addVector(vForward.crossProduct(new Vec3D(0, 0.1, 0))));
        }

        if (keyH.downPressed)
        {
            vCamera.getCam().y += 0.1;
        }

        if (keyH.upPressed)
        {
            vCamera.getCam().y -= 0.1;
        }

        if (keyH.frontPressed)
        {
            vCamera.setCam(vCamera.getCam().addVector(vForward));
        }

        if (keyH.backPressed)
        {
            vCamera.setCam(vCamera.getCam().subtractVector(vForward));
        }

        if (keyH.rightTurn)
        {
            fYaw -= 0.008;
        }

        if (keyH.leftTurn)
        {
            fYaw += 0.008;
        }

        if (keyH.upTurn)
        {
            fPitch -= 0.008;
        }

        if (keyH.downTurn)
        {
            fPitch += 0.008;
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Fill the screen background
        slFill(pixels, CD_BLACK);

        List<Triangle> vecTrianglesToRaster = new ArrayList<>();

        // rotation matrix
        matZ = Matrix.rotateMatrixZ(fTheta * 0.5);
        matZX = Matrix.rotateMatrixX(fTheta);

        // distance from cube -> translation matrix
        Matrix trans = Matrix.translationMatrix(0, 0, 1);

        // Matrix Matrix multiplication to accumulate multiple transformations
        Matrix matWorld = matZ.multiply(matZX);
        matWorld = matWorld.multiply(trans);

        Matrix matView = rasterAssembler.calculateViewMatrix(fPitch, fYaw, vCamera, vLookDir);

        for(Mesh meshCube: polygonGroup.getPolyGroup())
        {
            for (Triangle tri : meshCube.triangles)
            {
                Triangle triProjection = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
                Triangle triTrans = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
                Triangle triViewed = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));

                // assemble World Matrix
                rasterAssembler.assembleWorldMatrix(triTrans, tri, matWorld);

                Vec3D line1 = new Vec3D();
                Vec3D line2 = new Vec3D();
                Vec3D normal = rasterAssembler.assembleVertexNormals(triTrans, line1, line2);

                Vec3D vCameraRay = triTrans.vec3D.subtractVector(vCamera.getCam());

                // how much is each triangle's surface normal projection onto the camera
                if (normal.dotProduct(vCameraRay) < 0.0)
                {
                    // directional lighting that specifies a direction as to where the light should project from
                    Vec3D light_direction = new Vec3D(0, 0, -1);
                    light_direction.normalizeVector();

                    double dp = Math.max(0.1, light_direction.dotProduct(normal));

                    //WORLD SPACE TO VIEW SPACE
                    rasterAssembler.assembleViewMatrix(triViewed, triTrans, matView);

                    //PROJECT 3D GEOMETRICAL DATA TO NORMALIZED 2D SCREEN
                    rasterAssembler.assembleProjectionMatrix(triProjection, triViewed, matProj);

                    //CLIP TRIANGLE AGAINST NEAR PLANE
                    int clippedTriangles;
                    Triangle[] clipped = this.getNearestPlane();

                    clippedTriangles = triViewed.triangleClipAgainstPlane(new Vec3D(0, 0, 0.1d), new Vec3D(0, 0, 1), clipped);

                    for (int n = 0; n < clippedTriangles; n++)
                    {
                        // project 3d geometrical data to normalize 2d screen
                        rasterAssembler.assembleClipSpace(triProjection, clipped, n, matProj);

                        // scale texture c
                        rasterAssembler.assemblePerspectiveDivide(triProjection);

                        // scale into view
                        rasterAssembler.scaleIntoView(triProjection);

                        // set the current lighting color
                        int value = (int) Math.abs(dp * 255);
                        triProjection.color(value, value, value);
                        triProjection.tex = tri.tex;
                        triProjection.dp = dp;

                        vecTrianglesToRaster.add(triProjection);
                    }
                }
            }

            zBuffer.resetBuffer();

            // print to screen
            for(Triangle t: vecTrianglesToRaster)
            {
                Triangle[] clipped;
                LinkedList<Triangle> listTriangles = new LinkedList<>();
                listTriangles.add(t);
                int nNewTriangles = 1;

                for (int p = 0; p < 4; p++)
                {
                    int trisToAdd;

                    while (nNewTriangles > 0)
                    {
                        clipped = this.getNearestPlane();

                        Triangle test = listTriangles.peek();
                        listTriangles.pollFirst();
                        nNewTriangles--;

                        assert test != null;
                        switch (p)
                        {
                            case 0 -> trisToAdd = test.triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), clipped);
                            case 1 -> trisToAdd = test.triangleClipAgainstPlane(new Vec3D(0, getImageHeight() - 1.0, 0), new Vec3D(0, -1, 0), clipped);
                            case 2 -> trisToAdd = test.triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), clipped);
                            case 3 -> trisToAdd = test.triangleClipAgainstPlane(new Vec3D(getImageWidth() - 1.0, 0, 0), new Vec3D(-1, 0, 0), clipped);
                            default -> throw new IllegalArgumentException("Invalid value for p: " + p);
                        }


                        listTriangles.addAll(Arrays.asList(clipped).subList(0, trisToAdd));
                    }
                    nNewTriangles = listTriangles.size();
                }

                for(Triangle triangle: listTriangles)
                {
                    TexturedTriangle((int) triangle.vec3D.x,(int) triangle.vec3D.y, triangle.vec2D.u, triangle.vec2D.v,triangle.vec2D.w,
                            (int) triangle.vec3D2.x,(int) triangle.vec3D2.y, triangle.vec2D2.u, triangle.vec2D2.v, triangle.vec2D2.w,
                            (int) triangle.vec3D3.x,(int) triangle.vec3D3.y, triangle.vec2D3.u, triangle.vec2D3.v, triangle.vec2D3.w,
                            triangle.tex, 0, false, false, pixels, zBuffer, triangle.dp);
                }
            }
        }

        // ask ImageProducer to update image
        mImageProducer.newPixels();
        g2.drawImage(this.imageBuffer, 0, 0, (int) getImageWidth(), (int) getImageHeight(), this);

        g.dispose();
    }

    public Triangle[] getNearestPlane()
    {
        return new Triangle[]
                {
                        new Triangle
                                (
                                        new Vec3D(0, 0, 0),
                                        new Vec3D(0, 0, 0),
                                        new Vec3D(0, 0, 0),
                                        new Vec2D(0, 0),
                                        new Vec2D(0, 0),
                                        new Vec2D(0, 0)
                                ),
                        new Triangle
                                (
                                        new Vec3D(0, 0, 0),
                                        new Vec3D(0, 0, 0),
                                        new Vec3D(0, 0, 0),
                                        new Vec2D(0, 0),
                                        new Vec2D(0, 0),
                                        new Vec2D(0, 0)
                                )
                };
    }
}
