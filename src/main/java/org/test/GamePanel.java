package org.test;

import org.test.gfx.Texture;
import org.test.gfx.ZBuffer;
import org.test.math.*;

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
    private Image imageBuffer;
    private MemoryImageSource mImageProducer;
    private ColorModel cm;
    private int[] pixels;
    private ZBuffer zBuffer = new ZBuffer((int) getImageWidth(), (int) getImageHeight());;

    // Frames per second
    double fps = 60;
    KeyHandler keyH = new KeyHandler();

    // Projection matrix
    double fNear = 0.1; // nearest clip plane
    double fFar = 1000.0;  // furthers clip plane
    double fov = 90.0; // flied of view
    double a = getImageHeight() / getImageWidth(); // aspect ratio of the window
    Matrix mat = new Matrix();
    Matrix matProj = mat.projektionMatrix(fNear, fFar, a, fov); // projection matrix
    Matrix matZ, matZX; // rotation matrix
    double fTheta; // angle used in rotation matrix
    Mesh meshCube; // collection of triangles that form an object
    PolygonGroup polygonGroup;
    Vec3D vCamera = new Vec3D(0, 0, 0); // stationary position vector, that will serve as the camera
    Vec3D vLookDir = new Vec3D(0, 0, 1); // camera that follows along the look at direction
    double fYaw = 0.0;
    double fPitch = 0.0;
    boolean drawEdges = true;

    public GamePanel()
    {
        cm = getCompatibleColorModel();

        // calculate the pixels depending on the current screen / Panel size
        int screenSize = (int) getImageWidth() * (int) getImageHeight();
        if(pixels == null || pixels.length < screenSize)
        {
            pixels = new int[screenSize];
        }

        // This class is an implementation of the ImageProducer interface which uses an array
        // to produce pixel values for an Image.
        mImageProducer =  new MemoryImageSource((int) getImageWidth(), (int) getImageHeight(), cm, pixels,0, (int) getImageWidth());
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

        meshCube = new Mesh(Arrays.asList
                (
                        new Triangle[]
                                {
                                        // SOUTH
                                        new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), new Vec3D(1, 1, 0), new Vec2D(0.0, 1.0), new Vec2D(0.0, 0.0), new Vec2D(1.0, 0.0)),
                                        new Triangle(new Vec3D(0, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 0, 0), new Vec2D(0.0, 1.0), new Vec2D(1.0, 0.0), new Vec2D(1.0, 1.0)),
                                        // EAST
                                        new Triangle(new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 1, 1), new Vec2D(0.0, 1.0), new Vec2D(0.0, 0.0), new Vec2D(1.0, 0.0)),
                                        new Triangle(new Vec3D(1, 0, 0), new Vec3D(1, 1, 1), new Vec3D(1, 0, 1), new Vec2D(0.0, 1.0), new Vec2D(1.0, 0.0), new Vec2D(1.0, 1.0)),
                                        // NORTH
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(1, 1, 1), new Vec3D(0, 1, 1), new Vec2D(0.0, 1.0), new Vec2D(0.0, 0.0), new Vec2D(1.0, 0.0)),
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1), new Vec2D(0.0, 1.0), new Vec2D(1.0, 0.0), new Vec2D(1.0, 1.0)),
                                        // WEST
                                        new Triangle(new Vec3D(0, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 1, 0), new Vec2D(0.0, 1.0), new Vec2D(0.0, 0.0), new Vec2D(1.0, 0.0)),
                                        new Triangle(new Vec3D(0, 0, 1), new Vec3D(0, 1, 0), new Vec3D(0, 0, 0), new Vec2D(0.0, 1.0), new Vec2D(1.0, 0.0), new Vec2D(1.0, 1.0)),
                                        // TOP
                                        new Triangle(new Vec3D(0, 1, 0), new Vec3D(0, 1, 1), new Vec3D(1, 1, 1), new Vec2D(0.0, 1.0), new Vec2D(0.0, 0.0), new Vec2D(1.0, 0.0)),
                                        new Triangle(new Vec3D(0, 1, 0), new Vec3D(1, 1, 1), new Vec3D(1, 1, 0), new Vec2D(0.0, 1.0), new Vec2D(1.0, 0.0), new Vec2D(1.0, 1.0)),
                                        // BOTTOM
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 0), new Vec2D(0.0, 1.0), new Vec2D(0.0, 0.0), new Vec2D(1.0, 0.0)),
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), new Vec2D(0.0, 1.0), new Vec2D(1.0, 0.0), new Vec2D(1.0, 1.0)),
                                })
        );

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
            t.tex = new Texture(img);
        }

        polygonGroup.addMesh(meshCube);
    }

    public void update()
    {
        Vec3D vForward = vLookDir.normalizeVector(vLookDir); // Normalisierte Blickrichtung

        if (keyH.rightPressed)
        {
            vCamera = vCamera.subtractVector(vCamera, vForward.crossProduct(vLookDir, new Vec3D(0, 0.1, 0)));
        }

        if (keyH.leftPressed)
        {
            vCamera = vCamera.addVector(vCamera, vForward.crossProduct(vLookDir, new Vec3D(0, 0.1, 0)));
        }

        if (keyH.downPressed)
        {
            vCamera.y += 0.1;
        }

        if (keyH.upPressed)
        {
            vCamera.y -= 0.1;
        }

        if (keyH.frontPressed)
        {
            vCamera = vCamera.addVector(vCamera, vForward);
        }

        if (keyH.backPressed)
        {
            vCamera = vCamera.subtractVector(vCamera, vForward);
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
        // g2.setColor(new Color(173, 216, 230));
        // g2.fillRect(0, 0, getWidth(), getHeight());

        // rotation matrix
        // fTheta += 0.0002;
        matZ = mat.rotateMatrixZ(fTheta * 0.5);
        matZX = mat.rotateMatrixX(fTheta);

        // distance from cube -> translation matrix
        Matrix trans = mat.translationMatrix(0, 0, 1);

        // Matrix Matrix multiplication to accumulate multiple transformations
        Matrix matWorld;
        matWorld = mat.identityMatrix();
        matWorld = matWorld.matrixMatrixMultiplication(matZ, matZX);
        matWorld = matWorld.matrixMatrixMultiplication(matWorld, trans);

        List<Triangle> vecTrianglesToRaster = new ArrayList<>();

        for(Mesh meshCube: polygonGroup.getPolyGroup())
        {
            Vec3D vUp = new Vec3D(0, 1, 0);
            Vec3D vTarget = new Vec3D(0, 0, 1);
            Matrix matCameraRot = mat.matrixMatrixMultiplication(mat.rotateMatrixX(fPitch), mat.rotateMatrixY(fYaw));
            vLookDir = mat.multiplyMatrixVector(vTarget, matCameraRot);
            vTarget = vTarget.addVector(vCamera, vLookDir);

            // using the information provided above to define a camera matrix
            Matrix matCamera = new Matrix();
            matCamera = matCamera.pointAtMatrix(vCamera, vTarget, vUp);

            Matrix matView = new Matrix();
            matView = matView.inverseMatrix(matCamera);

            for (Triangle tri : meshCube.triangles)
            {
                Triangle triProjection = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
                Triangle triTrans = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
                Triangle triViewed = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));

                // assemble World Matrix
                // TODO: extract method
                triTrans.vec3D = mat.multiplyMatrixVector(tri.vec3D, matWorld);
                triTrans.vec3D2 = mat.multiplyMatrixVector(tri.vec3D2, matWorld);
                triTrans.vec3D3 = mat.multiplyMatrixVector(tri.vec3D3, matWorld);
                triTrans.vec2D = tri.vec2D;
                triTrans.vec2D2 = tri.vec2D2;
                triTrans.vec2D3 = tri.vec2D3;

                // collect surface normals and see how much they map over the camera
                Vec3D normal = new Vec3D(0, 0, 0);
                Vec3D line1 = new Vec3D(0, 0, 0);
                Vec3D line2 = new Vec3D(0, 0, 0);

                line1 = line1.subtractVector(triTrans.vec3D2, triTrans.vec3D);
                line2 = line1.subtractVector(triTrans.vec3D3, triTrans.vec3D);

                normal = line1.crossProduct(line1, line2);
                normal = line1.normalizeVector(normal);

                Vec3D vCameraRay = line1.subtractVector(triTrans.vec3D, vCamera);

                // how much is each triangle's surface normal projection onto the camera
                if (line1.dotProduct(normal, vCameraRay) < 0.0)
                {
                    // directional lighting that specifies a direction as to where the light should project from
                    Vec3D light_direction = new Vec3D(0, 0, -1);
                    light_direction.normalizeVector(light_direction);

                    double dp = Math.max(0.1, line1.dotProduct(light_direction, normal));

                    // convert world space to view space
                    // TODO: extract method
                    triViewed.vec3D = matView.multiplyMatrixVector(triTrans.vec3D, matView);
                    triViewed.vec3D2 = matView.multiplyMatrixVector(triTrans.vec3D2, matView);
                    triViewed.vec3D3 = matView.multiplyMatrixVector(triTrans.vec3D3, matView);
                    triViewed.vec2D = triTrans.vec2D;
                    triViewed.vec2D2 = triTrans.vec2D2;
                    triViewed.vec2D3 = triTrans.vec2D3;

                    // project 3d to 2d screen
                    triProjection.vec3D = mat.multiplyMatrixVector(triViewed.vec3D, matProj);
                    triProjection.vec3D2 = mat.multiplyMatrixVector(triViewed.vec3D2, matProj);
                    triProjection.vec3D3 = mat.multiplyMatrixVector(triViewed.vec3D3, matProj);

                    // Clip viewed triangle against near plane
                    int clippedTriangles;
                    Triangle[] clipped = new Triangle[]{new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0))
                            , new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0))};

                    clippedTriangles = line1.triangleClipAgainstPlane(new Vec3D(0, 0, 0.1d), new Vec3D(0, 0, 1), triViewed, clipped);

                    for (int n = 0; n < clippedTriangles; n++)
                    {
                        // project 3d geometrical data to normalize 2d screen
                        triProjection.vec3D = mat.multiplyMatrixVector(clipped[n].vec3D, matProj);
                        triProjection.vec3D2 = mat.multiplyMatrixVector(clipped[n].vec3D2, matProj);
                        triProjection.vec3D3 = mat.multiplyMatrixVector(clipped[n].vec3D3, matProj);
                        triProjection.vec2D = (Vec2D) clipped[n].vec2D.clone();
                        triProjection.vec2D2 = (Vec2D) clipped[n].vec2D2.clone();
                        triProjection.vec2D3 = (Vec2D) clipped[n].vec2D3.clone();

                        // scale texture c
                        triProjection.vec2D.u = triProjection.vec2D.u / triProjection.vec3D.w;
                        triProjection.vec2D2.u = triProjection.vec2D2.u / triProjection.vec3D2.w;
                        triProjection.vec2D3.u = triProjection.vec2D3.u / triProjection.vec3D3.w;
                        triProjection.vec2D.v = triProjection.vec2D.v / triProjection.vec3D.w;
                        triProjection.vec2D2.v = triProjection.vec2D2.v / triProjection.vec3D2.w;
                        triProjection.vec2D3.v = triProjection.vec2D3.v / triProjection.vec3D3.w;

                        triProjection.vec2D.w = 1.0d / triProjection.vec3D.w;
                        triProjection.vec2D2.w = 1.0d / triProjection.vec3D2.w;
                        triProjection.vec2D3.w = 1.0d / triProjection.vec3D3.w;

                        triProjection.vec3D = line1.divideVector(triProjection.vec3D, triProjection.vec3D.w);
                        triProjection.vec3D2 = line1.divideVector(triProjection.vec3D2, triProjection.vec3D2.w);
                        triProjection.vec3D3 = line1.divideVector(triProjection.vec3D3, triProjection.vec3D3.w);

                        // scale into view
                        triProjection.vec3D.x += 1.0;
                        triProjection.vec3D2.x += 1.0;
                        triProjection.vec3D3.x += 1.0;
                        triProjection.vec3D.y += 1.0;
                        triProjection.vec3D2.y += 1.0;
                        triProjection.vec3D3.y += 1.0;

                        triProjection.vec3D.x *= 0.5 * getImageWidth();
                        triProjection.vec3D.y *= 0.5 * getImageHeight();
                        triProjection.vec3D2.x *= 0.5 * getImageWidth();
                        triProjection.vec3D2.y *= 0.5 * getImageHeight();
                        triProjection.vec3D3.x *= 0.5 * getImageWidth();
                        triProjection.vec3D3.y *= 0.5 * getImageHeight();

                        // set the current lighting color
                        int value = (int) Math.abs(dp * 255);
                        triProjection.color(value, value, value);
                        triProjection.tex = tri.tex;
                        triProjection.dp = dp;

                        vecTrianglesToRaster.add(triProjection);
                    }
                }
            }

            // sort them by the distance from the camera -> with the death buffer we dont need to sort them anymore
            vecTrianglesToRaster.sort((o1, o2) ->
            {
                double z1 = (o1.vec3D.z + o1.vec3D2.z + o1.vec3D3.z) / 3.0;
                double z2 = (o2.vec3D.z + o2.vec3D2.z + o2.vec3D3.z) / 3.0;

                return (z1 > z2) ? 1 : (z1 == z2) ? 0 : -1;
            });

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
                        clipped = new Triangle[]{new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0)),
                                new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0))};

                        Triangle test = listTriangles.peek();
                        listTriangles.pollFirst();
                        nNewTriangles--;

                        Vec3D vec = new Vec3D(0, 0, 0);

                        assert test != null;
                        switch (p)
                        {
                            case 0 -> trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), test, clipped);
                            case 1 -> trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0, getImageHeight() - 1.0, 0), new Vec3D(0, -1, 0), test, clipped);
                            case 2 -> trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), test, clipped);
                            case 3 -> trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(getImageWidth() - 1.0, 0, 0), new Vec3D(-1, 0, 0), test, clipped);
                            default -> throw new IllegalArgumentException("Invalid value for p: " + p);
                        }


                        for (int w = 0; w < trisToAdd; w++)
                        {
                            listTriangles.add(clipped[w]);
                        }
                    }
                    nNewTriangles = listTriangles.size();
                }

                for(Triangle triangle: listTriangles)
                {
                    TexturedTriangle(g2, (int) triangle.vec3D.x,(int) triangle.vec3D.y, triangle.vec2D.u, triangle.vec2D.v,triangle.vec2D.w,
                            (int) triangle.vec3D2.x,(int) triangle.vec3D2.y, triangle.vec2D2.u, triangle.vec2D2.v, triangle.vec2D2.w,
                            (int) triangle.vec3D3.x,(int) triangle.vec3D3.y, triangle.vec2D3.u, triangle.vec2D3.v, triangle.vec2D3.w,
                            triangle.tex, 0, false, false, pixels, zBuffer, triangle.tex.getTexArray(), triangle.dp);
                }
            }
        }

        // ask ImageProducer to update image
        mImageProducer.newPixels();
        g2.drawImage(this.imageBuffer, 0, 0, (int) getImageWidth(), (int) getImageHeight(), this);

        g.dispose();
    }

    /** OLD DRAWING
     *                 if (!drawEdges)
     *                 {
     *                     g2.setColor(Color.BLACK);
     *
     *                     // draw edges of the triangle
     *                     drawTriangle
     *                             (
     *                                     g2,
     *                                     triangle.vec3D.x,
     *                                     triangle.vec3D.y,
     *                                     triangle.vec3D2.x,
     *                                     triangle.vec3D2.y,
     *                                     triangle.vec3D3.x,
     *                                     triangle.vec3D3.y
     *                             );
     *                 }
     *
     *                 g2.setColor(triangle.color);
     *
     *                 Polygon pol = new Polygon();
     *                 pol.addPoint((int) triangle.vec3D.x, (int) triangle.vec3D.y);
     *                 pol.addPoint((int) triangle.vec3D2.x, (int) triangle.vec3D2.y);
     *                 pol.addPoint((int) triangle.vec3D3.x, (int) triangle.vec3D3.y);
     *
     *                 g2.fill(pol);
     */
}
