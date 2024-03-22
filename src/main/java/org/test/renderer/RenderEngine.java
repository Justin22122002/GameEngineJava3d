package org.test.renderer;

import org.test.math.triangle.DrawMode;
import org.test.math.triangle.Mesh;
import org.test.math.triangle.PolygonGroup;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;
import org.test.objects.AbstractObject;
import org.test.renderdata.RenderSettings;
import org.test.scene.AbstractScene;

import javax.swing.*;

import java.util.List;
import java.util.Map;

import static org.test.renderer.PanelConfig.getImageHeight;
import static org.test.renderer.PanelConfig.getImageWidth;

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
        var poly = new PolygonGroup();

        for (AbstractObject object: scene.initializeObjects())
        {
            poly.getPolyGroup().addAll(object.getMeshes());
        }
        settings.setPolygonGroup(poly);
    }

    public void update()
    {
        scene.update();
        Map<List<Triangle>, DrawMode> trisToRender = rasterizer.raster();
        renderer.render(trisToRender);
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
