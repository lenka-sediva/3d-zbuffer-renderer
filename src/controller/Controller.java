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
    private Mat4 model, proj;
    //private FragmentShader constantColor, smoothColor, textureColor;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.renderer = new Renderer3D();

        this.arrow = new Arrow();
        this.tetrahedron = new Tetrahedron();

        this.triangleRasterizer = new TriangleRasterizer(vB);
        this.lineRasterizer = new LineRasterizerBresenham(vB);

        view = new Camera(new Vec3D(0, 0, 0), Math.PI * 1.25, Math.PI * -0.125, 3, false);

        initListeners(canvas);
        redraw();
    }

    private void initObjects(ColorBuffer raster) {
        vB.clear();

        // šipka
        renderer.setTriangleRasterizer(triangleRasterizer);
        renderer.setLineRasterizer(lineRasterizer);
        renderer.setViewport(canvas.getWidth(), canvas.getHeight());

        model = new Mat4Identity();

        // transformační matice
        proj = new Mat4PerspRH(
                Math.toRadians(45),
                (double) canvas.getHeight() / canvas.getWidth(),
                1.0, // zNear
                15. // zFar (very close, for clipping test)
        );

        renderer.renderSolid(arrow, model, view, proj);
        renderer.renderSolid(tetrahedron, model, view, proj);
    }

    private void initListeners(Canvas canvas) {
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // pohyby kamery WASD
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        view = view.forward(0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_A:
                        view = view.left(0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_S:
                        view = view.backward(0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_D:
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
