package org.test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class KeyHandler implements KeyListener, MouseMotionListener
{
    private int mouseX, mouseY;
    public boolean upPressed, rightPressed, downPressed, leftPressed, frontPressed, backPressed, rightTurn, leftTurn, downTurn, upTurn;
    @Override
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_A)
        {
            leftPressed = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_D)
        {
            rightPressed = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            upPressed = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            downPressed = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_W)
        {
            frontPressed = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_S)
        {
            backPressed = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_E)
        {
            rightTurn = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_Q)
        {
            leftTurn = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_T)
        {
            upTurn = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_G)
        {
            downTurn = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_D)
        {
            rightPressed = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_A)
        {
            leftPressed = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            upPressed = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            downPressed = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_W)
        {
            frontPressed = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_S)
        {
            backPressed = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_E)
        {
            rightTurn = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_Q)
        {
            leftTurn = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_T)
        {
            upTurn = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_G)
        {
            downTurn = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {

    }

    @Override
    public void mouseMoved(MouseEvent e)
    {

    }
}
