package org.test;

import org.test.math.Matrix;
import org.test.math.Mesh;
import org.test.math.Triangle;
import org.test.math.Vec3D;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static org.test.gfx.DrawUtils.drawTriangle;

public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;

    // Frames per second
    double fps = 60;
    KeyHandler kexH = new KeyHandler();

    // Projection matrix
    double fNear = 0.1; // nearest clip plane
    double fFar = 1000.0;  // furthers clip plane
    double fov = 90.0; // flied of view
    double a = 600.0 / 800.0; // aspect ratio of the window
    Matrix mat = new Matrix();
    Matrix matProj = mat.projektionMatrix(fNear, fFar, a, fov); // projection matrix
    Matrix matZ, matZX; // rotation matrix
    double fTheta; // angle used in rotation matrix
    Mesh meshCube; // collection of triangles that form an object

    public GamePanel()
    {
        this.addKeyListener(kexH);
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
                                new Triangle(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0))
                        })
                );
    }

    public void update()
    {

    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);

        // Fill the screen black
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, 800, 600);

        // rotation matrix
        fTheta += 0.02;
        matZ = mat.rotateMatrixZ(fTheta * 0.5);
        matZX = mat.rotateMatrixX(fTheta);

        // distance from cube -> translation matrix
        Matrix trans = mat.translationMatrix(0, 0, 16);

        // Matrix Matrix multiplication to accumulate multiple transformations
        Matrix matWorld;
        matWorld = mat.identityMatrix();
        matWorld = matWorld.matrixMatrixMultiplication(matZ, matZX);
        matWorld = matWorld.matrixMatrixMultiplication(matWorld, trans);

        for (Triangle tri : meshCube.triangles)
        {
            Triangle triProjection = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));
            Triangle triTrans = new Triangle(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0));

            triTrans.vec3D = mat.multiplyMatrixVector(tri.vec3D, matWorld);
            triTrans.vec3D2 = mat.multiplyMatrixVector(tri.vec3D2, matWorld);
            triTrans.vec3D3 = mat.multiplyMatrixVector(tri.vec3D3, matWorld);

            triProjection.vec3D = mat.multiplyMatrixVector(triTrans.vec3D, matProj);
            triProjection.vec3D2 = mat.multiplyMatrixVector(triTrans.vec3D2, matProj);
            triProjection.vec3D3 = mat.multiplyMatrixVector(triTrans.vec3D3, matProj);

            // scale into view
            triProjection.vec3D.x += 1.0;
            triProjection.vec3D2.x += 1.0;
            triProjection.vec3D3.x += 1.0;
            triProjection.vec3D.y += 1.0;
            triProjection.vec3D2.y += 1.0;
            triProjection.vec3D3.y += 1.0;

            triProjection.vec3D.x *= 0.5 * 800.0;
            triProjection.vec3D.y *= 0.5 * 600.0;
            triProjection.vec3D2.x *= 0.5 * 800.0;
            triProjection.vec3D2.y *= 0.5 * 600.0;
            triProjection.vec3D3.x *= 0.5 * 800.0;
            triProjection.vec3D3.y *= 0.5 * 600.0;

            g2.setColor(Color.WHITE);

            drawTriangle
                    (
                            g2,
                            triProjection.vec3D.x,
                            triProjection.vec3D.y,
                            triProjection.vec3D2.x,
                            triProjection.vec3D2.y,
                            triProjection.vec3D3.x,
                            triProjection.vec3D3.y
                    );
        }

        g.dispose();
    }

}
