package controller;

import app.ZBufferTest;
import model.rasterdata.ColorBuffer;
import model.rasterdata.Visibility;
import model.rasterops.TriangleRasterizer;
import transforms.Col;
import transforms.Point3D;
import view.Canvas;

import java.awt.*;

public class Controller {
    private final Canvas canvas;
    private Visibility vB;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());

        initListeners(canvas);
        redraw();
    }

    private void initObjects(ColorBuffer raster) {
        vB.clear();

        //ZBufferTest.runTest(vB); // barevné obdélníky

        // vrcholy pro první trojúhelník
        Point3D t1a = new Point3D(0., ((double) canvas.getHeight() / 2) - 1, 0.5);
        Point3D t1b = new Point3D(((double) canvas.getWidth() / 2) - 1, 0., 0.5);
        Point3D t1c = new Point3D(canvas.getWidth() - 1, canvas.getHeight() - 1, 0.5);

        // vrcholy pro druhý trojúhelník
        Point3D t2a = new Point3D(canvas.getWidth() - 1, ((double) canvas.getHeight() / 2) - 1, 0.8);
        Point3D t2b = new Point3D(((double) canvas.getWidth() / 2) - 1, canvas.getHeight() - 1, 0.8);
        Point3D t2c = new Point3D(0., 0., 0.1);

        // rasterizuj první trojúhelník s funkčním z-testem - červený
        TriangleRasterizer.rasterize(vB, t1a, t1b, t1c, new Col(1., 0, 0));
        // rasterizuj druhý trojúhelník s funkčním z-testem - žlutý
        TriangleRasterizer.rasterize(vB, t2a, t2b, t2c, new Col(1., 1., 0));
    }

    private void initListeners(Canvas canvas) {
        // TODO
    }

    private void redraw() {
        canvas.clear();

        initObjects(canvas.getRaster());

        canvas.repaint();
    }
}
