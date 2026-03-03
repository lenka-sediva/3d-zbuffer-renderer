package controller;

import app.ZBufferTest;
import model.objectdata.Solid;
import model.objectdata.Vertex;
import model.objects.Arrow;
import model.rasterdata.ColorBuffer;
import model.rasterdata.Visibility;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerBresenham;
import model.rasterops.TriangleRasterizer;
import model.renderer.Renderer3D;
import transforms.Col;
import transforms.Point3D;
import view.Canvas;

import java.awt.*;

public class Controller {
    private final Canvas canvas;
    private final Visibility vB;
    private final TriangleRasterizer triangleRasterizer;
    private final LineRasterizer lineRasterizer;
    private final Solid arrow;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.arrow = new Arrow();

        this.triangleRasterizer = new TriangleRasterizer(vB);
        // this.lineRasterizer = new LineRasterizerBresenham(vB); // nejde
        this.lineRasterizer = new LineRasterizerBresenham(canvas.getRaster());

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
        Renderer3D renderer = new Renderer3D();
        renderer.setTriangleRasterizer(triangleRasterizer);
        renderer.setLineRasterizer(lineRasterizer);

        renderer.renderSolid(arrow);
    }

    private void initListeners(Canvas canvas) {
        canvas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                double x = (2.0 * e.getX() / canvas.getWidth()) - 1.0;
                double y = 1.0 - (2.0 * e.getY() / canvas.getHeight());
                moveCenterVertex(x, y);
                redraw();
            }
        });

        canvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                double x = (2.0 * e.getX() / canvas.getWidth()) - 1.0;
                double y = 1.0 - (2.0 * e.getY() / canvas.getHeight());
                moveCenterVertex(x, y);
                redraw();
            }
        });
    }

    private void moveCenterVertex(double x, double y) {
        Vertex center = arrow.getVertexBuffer().get(0); // sdílený bílý posuvný vrchol ve středu šipky
        Vertex head = arrow.getVertexBuffer().get(18);  // pomocný bod u hlavičky

        // omezení x mezi start a hlavičku (vertex 18)
        double minX = -0.1; // start středu
        double maxX = head.getPosition().getX();

        if (x < minX) x = minX;
        if (x > maxX) x = maxX;

        // fixní y = y hlavičky
        double fixedY = head.getPosition().getY();

        Point3D newPosition = new Point3D(x, fixedY, center.getPosition().getZ());
        center.setPosition(newPosition);
    }

    private void redraw() {
        canvas.clear();

        initObjects(canvas.getRaster());

        canvas.repaint();
    }
}
