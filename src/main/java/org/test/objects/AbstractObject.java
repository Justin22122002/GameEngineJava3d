package org.test.objects;

import org.test.math.triangle.Mesh;
import org.test.math.triangle.Triangle;
import org.test.math.vector.Vector3D;

import java.util.List;

public abstract class AbstractObject
{
    private List<Mesh> meshes;

    public AbstractObject()
    {
        meshes = this.loadObject();
    }

    protected abstract List<Mesh> loadObject();
    public abstract void update();

    public void move(Vector3D translationVector)
    {
        for (Mesh mesh : meshes)
        {
            for (Triangle triangle : mesh.triangles)
            {
                triangle.vec3D = translationVector.addVector(triangle.vec3D);
                triangle.vec3D2 = translationVector.addVector(triangle.vec3D2);
                triangle.vec3D3 = translationVector.addVector(triangle.vec3D3);
            }
        }
    }

    public List<Mesh> getMeshes()
    {
        return meshes;
    }

    public void setMeshes(List<Mesh> meshes)
    {
        this.meshes = meshes;
    }
}
