package org.test.renderer;

import org.test.renderdata.RenderSettings;

import javax.swing.*;
import java.awt.*;

import static org.test.renderer.ImageConfig.getImageHeight;
import static org.test.renderer.ImageConfig.getImageWidth;

public class RenderPanel extends JPanel
{
    private final RenderSettings settings = RenderSettings.getInstance();

    public RenderPanel()
    {
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

        g2.drawImage(settings.getImageBuffer(), 0, 0, (int) getImageWidth(), (int) getImageHeight(), this);

        g.dispose();
    }
}
