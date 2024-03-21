package org.test;

import org.test.renderer.RenderEngine;
import org.test.scene.Scene;

public class Main
{
    public static void main(String[] args)
    {
        Scene scene = new Scene();

        var engine = new RenderEngine(scene);
        engine.startThread();
    }
}