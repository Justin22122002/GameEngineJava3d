package org.test;

import javax.swing.*;

public class Main
{
    private static final double WIDTH = 1440.0;
    private static final double HEIGHT = 1080.0;
    private static final String TITLE = "3dGameEngine";

    public static void main(String[] args)
    {
        GamePanel gp = new GamePanel();
        JFrame jFrame = new JFrame();
        jFrame.setSize((int) WIDTH, (int) HEIGHT);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle(TITLE);
        jFrame.setLocationRelativeTo(null);
        jFrame.add(gp);
        jFrame.setVisible(true);

        gp.startThread();
    }

    public static double getImageWidth()
    {
        return Main.WIDTH;
    }

    public static double getImageHeight()
    {
        return Main.HEIGHT;
    }
}