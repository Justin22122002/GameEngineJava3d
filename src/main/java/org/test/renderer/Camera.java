package org.test.renderer;

import org.test.math.Vec3D;

public class Camera
{
    private Vec3D cam;

    public Camera(double x, double y, double z)
    {
        cam = new Vec3D(0,0,0);

        cam.x = x;
        cam.y = y;
        cam.z = z;
    }

    public Camera()
    {
        cam = new Vec3D(0,0,0);
    }

    public Vec3D getCam()
    {
        return cam;
    }

    public void setCam(Vec3D cam)
    {
        this.cam = cam;
    }
}
