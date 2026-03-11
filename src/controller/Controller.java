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
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final Canvas canvas;
    private final Visibility vB;

    private final TriangleRasterizer triangleRasterizer;
    private final LineRasterizer lineRasterizer;

    private final Solid arrow, tetrahedron, sphere, cylinder;
    private Solid currentObject;
    private int currentObjectIndex = 0;
    private List<Solid> solids;

    private final Renderer3D renderer;
    private Camera view;
    private Mat4 model, proj, persp, ortho;
    private enum ProjectionType { PERSPECTIVE, ORTHOGONAL };
    private ProjectionType currentProjection = ProjectionType.PERSPECTIVE;
    private FragmentShader constantColor, smoothColor, textureColor;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.renderer = new Renderer3D();

        this.arrow = new Arrow();
        this.tetrahedron = new Tetrahedron();
        this.sphere = new Sphere(new Point3D(-1,2,0), 1.0, 20, 20);
        this.cylinder = new Cylinder(new Point3D(-5,0,0), 1.0, 2.0, 20);

        solids = new ArrayList<>();
        solids.add(arrow);
        solids.add(tetrahedron);
        solids.add(sphere);
        solids.add(cylinder);

        canvas.setObjects(solids);
        currentObject = solids.get(currentObjectIndex);
        currentObject.setSelected(true);

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

        persp = new Mat4PerspRH(Math.toRadians(90), (double) canvas.getHeight() / canvas.getWidth(), 0.1, 100); // perspektivní matice projekce
        ortho = new Mat4OrthoRH(canvas.getWidth() / 100., canvas.getHeight() / 100., 0.1, 100); // ortogonální matice projekce

        constantColor = new ConstantColor();
        smoothColor = new SmoothColor();
        textureColor = new TextureColor("res/mc_grass.jpg");

        triangleRasterizer.setShader(smoothColor::getColor);

        Mat4 usedProj = currentProjection == ProjectionType.PERSPECTIVE ? persp : ortho;

        renderer.renderSolid(arrow, model, view, usedProj);
        renderer.renderSolid(tetrahedron, model, view, usedProj);
        renderer.renderSolid(sphere, model, view, usedProj);
        renderer.renderSolid(cylinder, model, view, usedProj);
    }

    private void initListeners(Canvas canvas) {
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // smazání objektů
                    case KeyEvent.VK_C:
                        hardClear();
                        break;

                    // vrácení objektů do původního stavu
                    case KeyEvent.VK_O:
                        for (Solid s : solids) {
                            s.setModelMatrix(new Mat4Identity());
                        }
                        redraw();
                        break;

                    // pohyby pozorovatele
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

                    case KeyEvent.VK_Q:
                        view = view.addAzimuth(0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_E:
                        view = view.addAzimuth(-0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_SHIFT:
                        view = view.addZenith(-0.1);
                        redraw();
                        break;

                    case KeyEvent.VK_CONTROL:
                        view = view.addZenith(0.1);
                        redraw();
                        break;

                    // změna projekce
                    case KeyEvent.VK_P:
                        currentProjection = (currentProjection == ProjectionType.PERSPECTIVE)
                                ? ProjectionType.ORTHOGONAL // ortogonální projekce (pravoúhlá) = vzdálenost od kamery nemění velikost objektu
                                : ProjectionType.PERSPECTIVE; // perspektivní projekce (výchozí) = vzdálenější objekty se jeví menší
                        redraw();
                        break;

                    // shadery
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

                    // výběr aktuálního objektu
                    case KeyEvent.VK_T:
                        select(tetrahedron, "čtyřstěn");
                        break;

                    case KeyEvent.VK_V:
                        select(cylinder, "válec");
                        break;

                    case KeyEvent.VK_K:
                        select(sphere, "koule");
                        break;

                    case KeyEvent.VK_I:
                        select(arrow, "šipka");
                        break;

                    // rotace
                    case KeyEvent.VK_X: rotateSelected(Math.toRadians(10), new Vec3D(1,0,0)); break;
                    case KeyEvent.VK_Y: rotateSelected(Math.toRadians(10), new Vec3D(0,1,0)); break;
                    case KeyEvent.VK_Z: rotateSelected(Math.toRadians(10), new Vec3D(0,0,1)); break;

                    // translace
                    case KeyEvent.VK_LEFT: moveSelected(-0.1,0,0); break;
                    case KeyEvent.VK_RIGHT: moveSelected(0.1,0,0); break;
                    case KeyEvent.VK_UP: moveSelected(0,0.1,0); break;
                    case KeyEvent.VK_DOWN: moveSelected(0,-0.1,0); break;

                    // scale
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_ADD: scaleSelected(1.1); break;

                    case KeyEvent.VK_MINUS:
                    case KeyEvent.VK_SUBTRACT: scaleSelected(0.9); break;
                }
            }
        });
    }

    // pomocná metoda, aplikuje transformaci na vybraný objekt
    private void transformSelected(Mat4 transform) {
        if (currentObject != null) {
            currentObject.setModelMatrix(currentObject.getModelMatrix().mul(transform));
            redraw();
        }
    }

    // posune objekt o x, y, z
    private void moveSelected(double x, double y, double z) {
        transformSelected(new Mat4Transl(x, y, z));
    }

    // natočení kolem osy podle vektoru
    private void rotateSelected(double angleRad, Vec3D axis) {
        Vec3D center = currentObject.getCenter();
        transformSelected(
                new Mat4Transl(center.opposite()) // přesun do středu
                        .mul(new Mat4Rot(angleRad, axis)) // rotace
                        .mul(new Mat4Transl(center))      // zpět
        );
    }

    // škálování kolem středu objektu
    private void scaleSelected(double factor) {
        Vec3D center = currentObject.getCenter();
        transformSelected(
                new Mat4Transl(center.opposite())
                        .mul(new Mat4Scale(factor))
                        .mul(new Mat4Transl(center))
        );
    }
    private void setSelectedObject(Solid obj) {
        // zruší výběr předchozích objektů
        for (Solid s : solids) {
            s.setSelected(false);
        }

        // označí nový objekt
        obj.setSelected(true);

        // aktualizuje currentObject
        currentObject = obj;
    }

    // vybere objekt a předá jeho název do canvas
    private void select(Solid obj, String name) {
        setSelectedObject(obj);
        canvas.setCurrentObject(name);
    }

    private void hardClear() {
        canvas.clear();

        for(Solid s : solids) {
            s.setModelMatrix(new Mat4());
        }

        redraw();
    }

    private void redraw() {
        canvas.clear();
        // TODO vB.clear(): call Depth and Color buffer clear methods

        initObjects(canvas.getRaster());

        // aktualizace textu
        canvas.setCurrentProjectionText(currentProjection == ProjectionType.PERSPECTIVE ?
                "Perspektivní projekce [P]" : "Ortogonální projekce [P]");

        canvas.repaint();
    }
}
