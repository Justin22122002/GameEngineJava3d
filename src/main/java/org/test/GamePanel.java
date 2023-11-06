package org.test;

import org.test.math.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static org.test.Main.getImageHeight;
import static org.test.Main.getImageWidth;
import static org.test.gfx.DrawUtils.drawTriangle;

public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;

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
    Vec3D vCamera = new Vec3D(0, 0, 0); // stationary position vector, that will serve as the camera
    Vec3D vLookDir = new Vec3D(0, 0, 1); // camera that follows along the look at direction
    double fYaw = 0.0;
    double fPitch = 0.0;
    boolean drawEdges = true;

    public GamePanel()
    {
        this.addKeyListener(keyH);
        this.addMouseMotionListener(keyH);
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        initializeMesh();
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
        /**
         *  List<Triangle> tris = new ArrayList<>();
         *  List<Vec3D> verts = new ArrayList<>();
         *
         *  meshCube.loadObjectFromFile(tris, verts, "test.txt", "ftest.txt");
         */

        meshCube = new Mesh(Arrays.asList
                (
                        new Triangle[]
                                {
                                        // SOUTH
                                        new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), new Vec3D(1, 1, 0)),
                                        new Triangle(new Vec3D(0, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 0, 0)),
                                        // EAST
                                        new Triangle(new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 1, 1)),
                                        new Triangle(new Vec3D(1, 0, 0), new Vec3D(1, 1, 1), new Vec3D(1, 0, 1)),
                                        // NORTH
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(1, 1, 1), new Vec3D(0, 1, 1)),
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1)),
                                        // WEST
                                        new Triangle(new Vec3D(0, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 1, 0)),
                                        new Triangle(new Vec3D(0, 0, 1), new Vec3D(0, 1, 0), new Vec3D(0, 0, 0)),
                                        // TOP
                                        new Triangle(new Vec3D(0, 1, 0), new Vec3D(0, 1, 1), new Vec3D(1, 1, 1)),
                                        new Triangle(new Vec3D(0, 1, 0), new Vec3D(1, 1, 1), new Vec3D(1, 1, 0)),
                                        // BOTTOM
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 0)),
                                        new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0)),
                                })
        );

        meshCube = new Mesh(meshCube.ReadOBJFile("teapot.obj", false));
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
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);

        // Fill the screen background
        g2.setColor(new Color(173, 216, 230));
        g2.fillRect(0, 0, getWidth(), getHeight());

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

        List<Triangle> vecTrianglesToRaster = new ArrayList<>();

        for (Triangle tri : meshCube.triangles)
        {
            Triangle triProjection = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
            Triangle triTrans = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
            Triangle triViewed = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0),
                    new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0));

            triTrans.vec3D = mat.multiplyMatrixVector(tri.vec3D, matWorld);
            triTrans.vec3D2 = mat.multiplyMatrixVector(tri.vec3D2, matWorld);
            triTrans.vec3D3 = mat.multiplyMatrixVector(tri.vec3D3, matWorld);

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
                triViewed.vec3D = matView.multiplyMatrixVector(triTrans.vec3D, matView);
                triViewed.vec3D2 = matView.multiplyMatrixVector(triTrans.vec3D2, matView);
                triViewed.vec3D3 = matView.multiplyMatrixVector(triTrans.vec3D3, matView);

                // Clip viewed triangle against near plane
                int clippedTriangles = 0;
                Triangle[] clipped = new Triangle[]{new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0))
                        , new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0))};

                clippedTriangles = line1.triangleClipAgainstPlane(new Vec3D(0, 0, 0.1d), new Vec3D(0, 0, 1), triViewed, clipped);

                for (int n = 0; n < clippedTriangles; n++)
                {
                    // project 3d geometrical data to normalize 2d screen
                    triProjection.vec3D = mat.multiplyMatrixVector(clipped[n].vec3D, matProj);
                    triProjection.vec3D2 = mat.multiplyMatrixVector(clipped[n].vec3D2, matProj);
                    triProjection.vec3D3 = mat.multiplyMatrixVector(clipped[n].vec3D3, matProj);

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

                    vecTrianglesToRaster.add(triProjection);
                }
            }
        }

        // sort them by the distance from the camera
        vecTrianglesToRaster.sort((o1, o2) ->
        {
            double z1 = o1.vec3D.z + o1.vec3D2.z + o1.vec3D3.z / 3.0;
            double z2 = o2.vec3D.z + o2.vec3D2.z + o2.vec3D3.z / 3.0;

            return (z1 < z2) ? 1 : (z1 == z2) ? 0 : -1;
        });

        // print to screen
        vecTrianglesToRaster.forEach(t ->
        {
            Triangle[] clipped;
            LinkedList<Triangle> listTriangles = new LinkedList<>();
            listTriangles.add(t);
            int nNewTriangles = 1;

            for (int p = 0; p < 4; p++)
            {
                int trisToAdd = 0;

                while (nNewTriangles > 0)
                {
                    clipped = new Triangle[]{new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0)),
                            new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec2D(0, 0), new Vec2D(0, 0), new Vec2D(0, 0))};

                    Triangle test = listTriangles.peek();

                    test.vec2D = new Vec2D(0, 0);
                    test.vec2D2 = new Vec2D(0, 0);
                    test.vec2D3 = new Vec2D(0, 0);

                    listTriangles.pollFirst();
                    nNewTriangles--;

                    Vec3D vec = new Vec3D(0, 0, 0);

                    switch (p)
                    {
                        case 0:
                        {
                            trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), test, clipped);
                        }
                        break;
                        case 1:
                        {
                            trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0, getImageHeight() - 1.0, 0), new Vec3D(0, -1, 0), test, clipped);
                        }
                        break;
                        case 2:
                        {
                            trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), test, clipped);
                        }
                        break;
                        case 3:
                        {
                            trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(getImageWidth() - 1.0, 0, 0), new Vec3D(-1, 0, 0), test, clipped);
                        }
                        break;
                    }

                    for (int w = 0; w < trisToAdd; w++)
                    {
                        clipped[w].color = t.color;
                        listTriangles.add(clipped[w]);
                    }
                }
                nNewTriangles = listTriangles.size();
            }

            listTriangles.forEach(triangle ->
            {
                if (!drawEdges)
                {
                    g2.setColor(Color.BLACK);

                    // draw edges of the triangle
                    drawTriangle
                            (
                                    g2,
                                    triangle.vec3D.x,
                                    triangle.vec3D.y,
                                    triangle.vec3D2.x,
                                    triangle.vec3D2.y,
                                    triangle.vec3D3.x,
                                    triangle.vec3D3.y
                            );
                }

                g2.setColor(triangle.color);

                Polygon pol = new Polygon();
                pol.addPoint((int) triangle.vec3D.x, (int) triangle.vec3D.y);
                pol.addPoint((int) triangle.vec3D2.x, (int) triangle.vec3D2.y);
                pol.addPoint((int) triangle.vec3D3.x, (int) triangle.vec3D3.y);

                g2.fill(pol);
            });

        });

        g.dispose();
    }
}
