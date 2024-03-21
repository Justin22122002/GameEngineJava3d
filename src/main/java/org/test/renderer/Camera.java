package org.test.renderer;

import org.test.math.vector.Vector3D;

import static org.test.Main.getImageHeight;
import static org.test.Main.getImageWidth;

public class Camera
{
    private Vector3D cam;

    private double fNear = 0.1; // nearest clip plane
    private double fFar = 1000.0;  // furthers clip plane
    private double fov = 90.0; // flied of view
    private double a = getImageHeight() / getImageWidth(); // aspect ratio of the window
    private double fYaw = 0.0;
    private double fPitch = 0.0;
    private double fTheta; // angle used in rotation matrix
    private Vector3D vLookDir; // camera that follows along the look at direction

    public Camera(double x, double y, double z)
    {
        cam = new Vector3D(0,0,0);
        vLookDir = new Vector3D(0, 0, 1);

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

    public double getfNear()
    {
        return fNear;
    }

    public void setfNear(double fNear)
    {
        this.fNear = fNear;
    }

    public double getfFar()
    {
        return fFar;
    }

    public void setfFar(double fFar)
    {
        this.fFar = fFar;
    }

    public double getFov()
    {
        return fov;
    }

    public void setFov(double fov)
    {
        this.fov = fov;
    }

    public double getA()
    {
        return a;
    }

    public void setA(double a)
    {
        this.a = a;
    }

    public double getfYaw()
    {
        return fYaw;
    }

    public void setfYaw(double fYaw)
    {
        this.fYaw = fYaw;
    }

    public double getfPitch()
    {
        return fPitch;
    }

    public void setfPitch(double fPitch)
    {
        this.fPitch = fPitch;
    }

    public double getfTheta()
    {
        return fTheta;
    }

    public void setfTheta(double fTheta)
    {
        this.fTheta = fTheta;
    }

    public Vector3D getvLookDir()
    {
        return vLookDir;
    }

    public void setvLookDir(Vector3D vLookDir)
    {
        this.vLookDir = vLookDir;
    }
}
