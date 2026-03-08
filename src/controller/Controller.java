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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller {
    private final Canvas canvas;
    private final Visibility vB;
    private final TriangleRasterizer triangleRasterizer;
    private final LineRasterizer lineRasterizer;
    private final Solid arrow;
    private final Solid tetrahedron;
    private final Renderer3D renderer;
    private Camera view;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.arrow = new Arrow();
        this.tetrahedron = new Tetrahedron();
        this.renderer = new Renderer3D();

        this.triangleRasterizer = new TriangleRasterizer(vB);
        // this.lineRasterizer = new LineRasterizerBresenham(vB); // nejde
        this.lineRasterizer = new LineRasterizerBresenham(vB);

        view = new Camera(new Vec3D(0, 0, 0), Math.PI * 1.25, Math.PI * -0.125, 3, false);

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
        Mat4 proj = new Mat4PerspRH(
                Math.toRadians(45),
                (double) canvas.getWidth() / canvas.getHeight(),
                0.1,
                100
        );
        // Oprava modelové matice pro šipku a tetrahedron
        Mat4 arrowModel = new Mat4Identity();
        Mat4 tetrahedronModel = new Mat4RotX(Math.PI); // otočení tetrahedronu

        renderer.renderSolid(arrow, arrowModel, view, proj);
        renderer.renderSolid(tetrahedron, tetrahedronModel, view, proj);
    }

    private void initListeners(Canvas canvas) {
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // pohyby kamery WASD
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_W:
                        view = view.forward(0.1);
                        redraw();
                        break;

                    case java.awt.event.KeyEvent.VK_A:
                        view = view.left(0.1);
                        redraw();
                        break;

                    case java.awt.event.KeyEvent.VK_S:
                        view = view.backward(0.1);
                        redraw();
                        break;

                    case java.awt.event.KeyEvent.VK_D:
                        view = view.right(0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_LEFT:
                        view = view.addAzimuth(-0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_RIGHT:
                        view = view.addAzimuth(0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_UP:
                        view = view.addZenith(-0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_DOWN:
                        view = view.addZenith(0.1);
                        redraw();
                        break;
                }
            }
        });
    }

    private void redraw() {
        canvas.clear();

        initObjects(canvas.getRaster());

        canvas.repaint();
    }
}
