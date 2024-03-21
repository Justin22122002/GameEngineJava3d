package org.test.scene;

import org.test.graphics.Texture;
import org.test.math.triangle.Mesh;
import org.test.math.triangle.PolygonGroup;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Scene extends AbstractScene
{
    @Override
    public void update()
    {
        Vector3D vForward = vCamera.getvLookDir().normalizeVector(); // Normalisierte Blickrichtung

        if (keyH.rightPressed)
        {
            vCamera.setPosition(vCamera.getPosition().subtractVector(vForward.crossProduct(new Vector3D(0, 0.1, 0))));
        }

        if (keyH.leftPressed)
        {
            vCamera.setPosition(vCamera.getPosition().addVector(vForward.crossProduct(new Vector3D(0, 0.1, 0))));
        }

        if (keyH.downPressed)
        {
            vCamera.getPosition().y += 0.1;
        }

        if (keyH.upPressed)
        {
            vCamera.getPosition().y -= 0.1;
        }

        if (keyH.frontPressed)
        {
            vCamera.setPosition(vCamera.getPosition().addVector(vForward));
        }

        if (keyH.backPressed)
        {
            vCamera.setPosition(vCamera.getPosition().subtractVector(vForward));
        }

        if (keyH.rightTurn)
        {
            vCamera.setfYaw(vCamera.getfYaw() - 0.008);
        }

        if (keyH.leftTurn)
        {
            vCamera.setfYaw(vCamera.getfYaw() + 0.008);
        }

        if (keyH.upTurn)
        {
            vCamera.setfPitch(vCamera.getfPitch() - 0.008);
        }

        if (keyH.downTurn)
        {
            vCamera.setfPitch(vCamera.getfPitch() + 0.008);
        }
    }

    @Override
    public PolygonGroup initializeObjects()
    {
        PolygonGroup polygonGroup = new PolygonGroup();

        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("./car4.png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        Mesh mesh = new Mesh(Mesh.ReadOBJFile("car4.obj", true));

        for(Triangle t : mesh.triangles)
        {
            assert img != null;
            t.tex = new Texture(img);
        }

        polygonGroup.addMesh(mesh);

        return polygonGroup;
    }
}
