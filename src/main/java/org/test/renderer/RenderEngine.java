package org.test.renderer;

import org.test.math.triangle.Triangle;
import org.test.renderdata.RenderSettings;
import org.test.scene.AbstractScene;

import javax.swing.*;

import java.util.List;

import static org.test.renderer.ImageConfig.getImageHeight;
import static org.test.renderer.ImageConfig.getImageWidth;

public class RenderEngine implements Runnable
{
    private final RenderSettings settings = RenderSettings.getInstance();
    private AbstractScene scene;
    private final Rasterizer rasterizer;
    private final Renderer renderer;
    private final RenderPanel renderPanel;

    public RenderEngine(AbstractScene scene)
    {
        this.scene = scene;
        this.scene.setKeyH(settings.getKeyH());
        this.scene.setvCamera(settings.getvCamera());

        rasterizer = new Rasterizer(new RasterAssembler());
        renderer = new Renderer();

        renderPanel = new RenderPanel();
        initializeMesh();

        JFrame jFrame = new JFrame();
        jFrame.setSize((int) getImageWidth(), (int) getImageHeight());
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Engine3D");
        jFrame.setLocationRelativeTo(null);
        jFrame.add(renderPanel);
        jFrame.setVisible(true);
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
                renderPanel.repaint();
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
        settings.setPolygonGroup(scene.initializeObjects());
    }

    public void update()
    {
        List<Triangle> trisToRender = rasterizer.raster();
        renderer.render(trisToRender);
        scene.update();
    }

    public AbstractScene getScene()
    {
        return scene;
    }

    public void setScene(AbstractScene scene)
    {
        this.scene = scene;
    }
}
