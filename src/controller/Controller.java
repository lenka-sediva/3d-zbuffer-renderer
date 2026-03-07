package controller;

import model.objectdata.Solid;
import model.objects.Tetrahedron;
import model.objects.Arrow;
import model.rasterdata.ColorBuffer;
import model.rasterdata.Visibility;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerBresenham;
import model.rasterops.TriangleRasterizer;
import model.renderer.Renderer3D;
import transforms.*;
import view.Canvas;

public class Controller {
    private final Canvas canvas;
    private final Visibility vB;
    private final TriangleRasterizer triangleRasterizer;
    private final LineRasterizer lineRasterizer;
    private final Solid arrow;
    private final Solid tetrahedron;
    private final Renderer3D renderer;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.arrow = new Arrow();
        this.tetrahedron = new Tetrahedron();
        this.renderer = new Renderer3D();

        this.triangleRasterizer = new TriangleRasterizer(vB);
        // this.lineRasterizer = new LineRasterizerBresenham(vB); // nejde
        this.lineRasterizer = new LineRasterizerBresenham(canvas.getRaster()); // TODO

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
        //TriangleRasterizer.rasterize(vB, t1a, t1b, t1c, new Col(1., 0, 0));
        // rasterizuj druhý trojúhelník s funkčním z-testem - žlutý
        //TriangleRasterizer.rasterize(vB, t2a, t2b, t2c, new Col(1., 1., 0));

        // šipka
        renderer.setTriangleRasterizer(triangleRasterizer);
        renderer.setLineRasterizer(lineRasterizer);
        renderer.setViewport(canvas.getWidth(), canvas.getHeight());

        // transformační matice
        Mat4 model = new Mat4Identity();
        Mat4 view = new Mat4Identity();
        Mat4 proj = new Mat4OrthoRH(1, 1, 0.1, 10); // jednoduchá ortografická projekce

        renderer.renderSolid(arrow, model, view, proj);
        renderer.renderSolid(tetrahedron, model, view, proj);
    }

    private void initListeners(Canvas canvas) {

    }

    private void redraw() {
        canvas.clear();

        initObjects(canvas.getRaster());

        canvas.repaint();
    }
}
