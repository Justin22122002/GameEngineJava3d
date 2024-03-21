package org.test;

import org.test.gamedata.RenderSettings;
import org.test.graphics.Texture;
import org.test.input.KeyHandler;
import org.test.math.triangle.Mesh;
import org.test.math.triangle.PolygonGroup;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;
import org.test.renderer.Camera;
import org.test.renderer.RasterAssembler;
import org.test.renderer.Rasterizer;
import org.test.renderer.Renderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable
{
    RenderSettings settings = RenderSettings.getInstance();
    Rasterizer rasterizer;
    Renderer renderer;

    public GamePanel()
    {
        rasterizer = new Rasterizer(new RasterAssembler());
        renderer = new Renderer();

        // Basic JPanel Configuration
        this.addKeyListener(settings.getKeyH());
        this.addMouseMotionListener(settings.getKeyH());
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        initializeMesh();
    }

    public void startThread()
    {
        settings.setGameThread(new Thread(this));
        settings.getGameThread().start();
    }

    @Override
    public void run()
    {
        double drawInterval = 1_000_000_000 / settings.getFps();
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        int timer = 0;
        int drawCount = 0;

        while (settings.getGameThread() != null)
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
        PolygonGroup polygonGroup = new PolygonGroup();

        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("./car4.png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        Mesh mesh = new Mesh(Mesh.ReadOBJFile("car4.obj", true));

        for(Triangle t : mesh.triangles)
        {
            assert img != null;
            t.tex = new Texture(img);
        }

        polygonGroup.addMesh(mesh);
        settings.setPolygonGroup(polygonGroup);
    }

    public void update()
    {
        Vector3D vForward = settings.getvCamera().getvLookDir().normalizeVector(); // Normalisierte Blickrichtung
        KeyHandler keyH = settings.getKeyH();
        Camera vCamera = settings.getvCamera();

        if (keyH.rightPressed)
        {
            vCamera.setCam(vCamera.getCam().subtractVector(vForward.crossProduct(new Vector3D(0, 0.1, 0))));
        }

        if (keyH.leftPressed)
        {
            vCamera.setCam(vCamera.getCam().addVector(vForward.crossProduct(new Vector3D(0, 0.1, 0))));
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
            vCamera.setfYaw(vCamera.getfYaw() - 0.008);
        }

        if (keyH.leftTurn)
        {
            vCamera.setfYaw(vCamera.getfYaw() + 0.008);
        }

        if (keyH.upTurn)
        {
            vCamera.setfPitch(vCamera.getfPitch() - 0.008);
        }

        if (keyH.downTurn)
        {
            vCamera.setfPitch(vCamera.getfPitch() + 0.008);
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        renderer.render(g2, rasterizer.raster(), this);

        g.dispose();
    }
}
