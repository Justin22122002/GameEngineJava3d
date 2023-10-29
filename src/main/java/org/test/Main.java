package org.test;

import javax.swing.*;

public class Main
{
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "3dGameEngine";

    public static void main(String[] args)
    {
        GamePanel gp = new GamePanel();
        JFrame jFrame = new JFrame();
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setTitle(TITLE);
        jFrame.setLocationRelativeTo(null);
        jFrame.add(gp);
        jFrame.setVisible(true);

        gp.startThread();
    }
}