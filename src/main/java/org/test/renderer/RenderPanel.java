package org.test.renderer;

import org.test.renderdata.RenderSettings;

import javax.swing.*;
import java.awt.*;

public class RenderPanel extends JPanel
{
    private final RenderSettings settings = RenderSettings.getInstance();
    private final Rasterizer rasterizer;
    private final Renderer renderer;

    public RenderPanel(Rasterizer rasterizer, Renderer renderer)
    {
        this.rasterizer = rasterizer;
        this.renderer = renderer;

        // Basic JPanel Configuration
        this.addKeyListener(settings.getKeyH());
        this.addMouseMotionListener(settings.getKeyH());
        this.setFocusable(true);
        this.setDoubleBuffered(true);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        renderer.render(g2, rasterizer.raster(), this);

        g.dispose();
    }
}
