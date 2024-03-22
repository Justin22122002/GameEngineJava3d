package org.test.objects;

import org.test.graphics.Texture;
import org.test.math.triangle.DrawMode;
import org.test.math.triangle.Mesh;
import org.test.math.triangle.Triangle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Car extends AbstractObject
{
    @Override
    protected List<Mesh> loadObject()
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("./assets/car4/car4.png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        Mesh carMesh = new Mesh(Mesh.ReadOBJFile("./assets/car4/car4.obj", true), DrawMode.TEXTURED);

        for(Triangle t : carMesh.triangles)
        {
            assert img != null;
            t.tex = new Texture(img);
        }

        return List.of(carMesh);
    }
}
