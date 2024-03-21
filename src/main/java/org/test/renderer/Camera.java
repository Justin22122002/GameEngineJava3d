package org.test.renderer;

import org.test.math.vector.Vector3D;

public class Camera
{
    private Vector3D cam;

    public Camera(double x, double y, double z)
    {
        cam = new Vector3D(0,0,0);

        cam.x = x;
        cam.y = y;
        cam.z = z;
    }

    public Camera()
    {
        cam = new Vector3D(0,0,0);
    }

    public Vector3D getCam()
    {
        return cam;
    }

    public void setCam(Vector3D cam)
    {
        this.cam = cam;
    }
}
