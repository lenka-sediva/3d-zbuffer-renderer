package controller;

import model.objectdata.Solid;
import model.objects.*;
import model.rasterdata.*;
import model.rasterops.*;
import model.renderer.Renderer3D;
import shader.*;
import transforms.*;
import view.Canvas;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller {
    private final Canvas canvas;
    private final Visibility vB;

    private final TriangleRasterizer triangleRasterizer;
    private final LineRasterizer lineRasterizer;

    private final Solid arrow, tetrahedron, sphere, cylinder;

    private final Renderer3D renderer;
    private Camera view;
    private Mat4 model, proj;
    private FragmentShader constantColor, smoothColor, textureColor;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.renderer = new Renderer3D();

        this.arrow = new Arrow();
        this.tetrahedron = new Tetrahedron();
        this.sphere = new Sphere(new Point3D(-1,2,0), 1.0, 20, 20);
        this.cylinder = new Cylinder(new Point3D(-5,0,0), 1.0, 2.0, 20);

        this.triangleRasterizer = new TriangleRasterizer(vB, v -> v.getColor());
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
                Math.toRadians(90),
                (double) canvas.getHeight() / canvas.getWidth(),
                1.0, // zNear
                15. // zFar (very close, for clipping test)
        );

        constantColor = new ConstantColor();
        smoothColor = new SmoothColor();
        textureColor = new TextureColor("res/mc_grass.jpg");

        triangleRasterizer.setShader(smoothColor::getColor);

        renderer.renderSolid(arrow, model, view, proj);
        renderer.renderSolid(tetrahedron, model, view, proj);
        renderer.renderSolid(sphere, model, view, proj);
        renderer.renderSolid(cylinder, model, view, proj);
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

                    case KeyEvent.VK_1:
                        triangleRasterizer.setShader(constantColor::getColor);
                        redraw();
                        break;

                    case KeyEvent.VK_2:
                        triangleRasterizer.setShader(smoothColor::getColor);
                        redraw();
                        break;

                    case KeyEvent.VK_3:
                        triangleRasterizer.setShader(textureColor::getColor);
                        redraw();
                        break;
                }
            }
        });
    }

    private void redraw() {
        canvas.clear();
        // TODO vB.clear(): call Depth and Color buffer clear methods

        initObjects(canvas.getRaster());

        canvas.repaint();
    }
}
