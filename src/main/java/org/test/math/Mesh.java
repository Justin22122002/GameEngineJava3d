package org.test.math;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Mesh
{
    public List<Triangle> triangles;

    public Mesh(List<Triangle> triangles)
    {
        this.triangles = triangles;
    }

    public Mesh()
    {
    }

    /**
     * !!! OBJ-Dateiformat !!!
     */
    public void loadObjectFromFile(List<Triangle> tris, List<Vec3D> verts, String fileName, String fileName2)
    {
        File file = new File(fileName);
        File file2 = new File(fileName2);

        try(Scanner scan = new Scanner(file);
            Scanner scan2 = new Scanner(file2))
        {
            while (scan.hasNextLine())
            {
                double x = - scan.nextDouble();
                double y = - scan.nextDouble();
                double z = scan.nextDouble();

                verts.add(new Vec3D(x, y, z));
            }

            while (scan2.hasNextLine())
            {
                int [] f = new int[3];
                f[0] = scan2.nextInt();
                f[1] = scan2.nextInt();
                f[2] = scan2.nextInt();

                tris.add(new Triangle(verts.get(f[0] - 1), verts.get(f[1] - 1), verts.get(f[2] - 1)));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
