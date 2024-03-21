package org.test.scene;

import org.test.input.KeyHandler;
import org.test.math.triangle.PolygonGroup;
import org.test.renderer.Camera;

public abstract class AbstractScene
{
    protected Camera vCamera;
    protected KeyHandler keyH;

    public abstract void update();
    public abstract PolygonGroup initializeObjects();

    public Camera getvCamera()
    {
        return vCamera;
    }

    public void setvCamera(Camera vCamera)
    {
        this.vCamera = vCamera;
    }

    public KeyHandler getKeyH()
    {
        return keyH;
    }

    public void setKeyH(KeyHandler keyH)
    {
        this.keyH = keyH;
    }
}
