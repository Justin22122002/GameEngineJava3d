package org.test.scene;

import org.test.math.vector.Vector3D;
import org.test.objects.AbstractObject;
import org.test.objects.Car;

import java.util.List;

public class Scene extends AbstractScene
{
    Car car1;

    @Override
    public void update()
    {
        Vector3D vForward = getvCamera().getvLookDir().normalizeVector(); // Normalisierte Blickrichtung

        if (keyH.rightPressed)
        {
            getvCamera().setPosition(getvCamera().getPosition().subtractVector(vForward.crossProduct(new Vector3D(0, 0.1, 0))));
        }

        if (keyH.leftPressed)
        {
            getvCamera().setPosition(getvCamera().getPosition().addVector(vForward.crossProduct(new Vector3D(0, 0.1, 0))));
        }

        if (keyH.downPressed)
        {
            getvCamera().getPosition().y += 0.1;
        }

        if (keyH.upPressed)
        {
            getvCamera().getPosition().y -= 0.1;
        }

        if (keyH.frontPressed)
        {
            getvCamera().setPosition(getvCamera().getPosition().addVector(vForward));
        }

        if (keyH.backPressed)
        {
            getvCamera().setPosition(getvCamera().getPosition().subtractVector(vForward));
        }

        if (keyH.rightTurn)
        {
            getvCamera().setfYaw(getvCamera().getfYaw() - 0.008);
        }

        if (keyH.leftTurn)
        {
            getvCamera().setfYaw(getvCamera().getfYaw() + 0.008);
        }

        if (keyH.upTurn)
        {
            getvCamera().setfPitch(getvCamera().getfPitch() - 0.008);
        }

        if (keyH.downTurn)
        {
            getvCamera().setfPitch(getvCamera().getfPitch() + 0.008);
        }

        car1.update();
    }

    @Override
    public List<AbstractObject> initializeObjects()
    {
        car1 = new Car();

        return List.of(car1);
    }
}
