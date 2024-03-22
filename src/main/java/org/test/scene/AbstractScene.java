package org.test.scene;

import org.test.input.KeyHandler;
import org.test.objects.AbstractObject;
import org.test.renderer.Camera;

import java.util.List;

public abstract class AbstractScene
{
    protected Camera vCamera;
    protected KeyHandler keyH;

    public abstract void update();
    public abstract List<AbstractObject> initializeObjects();

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
