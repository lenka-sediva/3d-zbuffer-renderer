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

    private final Solid tetrahedron, sphere, cylinder, cube, axis;
    private Solid currentObject;
    private final List<Solid> solids;

    private final Renderer3D renderer;
    private Camera view;
    private Mat4 model, persp, ortho;
    private enum ProjectionType { PERSPECTIVE, ORTHOGONAL }
    private ProjectionType currentProjection = ProjectionType.PERSPECTIVE;
    private enum ModeType { SOLID, WIREFRAME }
    private ModeType currentMode = ModeType.SOLID;
    private final FragmentShader constantColor, smoothColor, grassTexture, woodTexture, uvTexture;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        this.vB = new Visibility(canvas.getRaster());
        this.renderer = new Renderer3D();

        this.tetrahedron = new Tetrahedron();
        this.sphere = new Sphere(new Point3D(-1,2,0), 1.0, 20, 20);
        this.cylinder = new Cylinder(new Point3D(-5,0,0), 1.0, 2.0, 20);
        this.cube = new Cube(new Point3D(2, 0, 0), 1.0);
        this.axis = new Axis();
        cube.setModelMatrix(new Mat4Rot(Math.toRadians(90), new Vec3D(1, 0, 0)));

        solids = new ArrayList<>();
        solids.add(tetrahedron);
        solids.add(sphere);
        solids.add(cylinder);
        solids.add(cube);

        canvas.setObjects(solids);
        currentObject = solids.getFirst();
        currentObject.setSelected(true);

        this.triangleRasterizer = new TriangleRasterizer(vB, v -> v.getColor());
        this.lineRasterizer = new LineRasterizerBresenham(vB);

        constantColor = new ConstantColor();
        smoothColor = new SmoothColor();
        grassTexture = new TextureColor("res/mc_grass.jpg");
        woodTexture = new TextureColor("res/plywood_diff_4k.jpg");
        uvTexture = new TextureColor("res/uv_grid.jpg");

        for (Solid s : solids) {
            s.setShader(smoothColor);
        }

        view = new Camera(new Vec3D(0, 0, 0), Math.PI * 1.25, Math.PI * -0.125, 3, false);

        initListeners(canvas);
        redraw();
    }

    private void initObjects() {
        vB.clear();

        renderer.setTriangleRasterizer(triangleRasterizer);
        renderer.setLineRasterizer(lineRasterizer);
        renderer.setViewport(canvas.getWidth(), canvas.getHeight());

        model = new Mat4Identity();

        persp = new Mat4PerspRH(Math.toRadians(90), (double) canvas.getHeight() / canvas.getWidth(), 0.1, 100); // perspektivní matice projekce
        ortho = new Mat4OrthoRH(canvas.getWidth() / 100., canvas.getHeight() / 100., 0.1, 100); // ortogonální matice projekce

        Mat4 proj = currentProjection == ProjectionType.PERSPECTIVE ? persp : ortho;

        // renderování všech objektů
        renderer.renderSolids(solids, model, view, proj);
        renderer.renderSolid(axis, model, view, proj);
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
                        canvas.setCurrentProjectionText(currentProjection == ProjectionType.PERSPECTIVE ?
                                "Perspektivní projekce [P]" : " Ortogonální projekce [P]");
                        redraw();
                        break;

                    // změna pohledu kamery = 1. a 3. osoba
                    case KeyEvent.VK_SPACE:
                        view = view.withFirstPerson(!view.getFirstPerson());
                        canvas.setCurrentCameraMode(view.getFirstPerson());
                        redraw();
                        break;

                    // wireframe přepínání
                    case KeyEvent.VK_M:
                        currentMode = (currentMode == ModeType.SOLID) ? ModeType.WIREFRAME : ModeType.SOLID;
                        renderer.setWireframe(currentMode == ModeType.WIREFRAME);
                        canvas.setCurrentMode(currentMode == ModeType.WIREFRAME ? "   Drátový model [M]" : "Vyplněný model [M]");
                        redraw();
                        break;

                    // shadery
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_NUMPAD1:
                        currentObject.setShader(constantColor);
                        redraw();
                        break;

                    case KeyEvent.VK_2:
                    case KeyEvent.VK_NUMPAD2:
                        currentObject.setShader(smoothColor);
                        redraw();
                        break;

                    case KeyEvent.VK_3:
                    case KeyEvent.VK_NUMPAD3:
                        if (currentObject == cube) currentObject.setShader(grassTexture);
                        if (currentObject == cylinder) currentObject.setShader(woodTexture);
                        if (currentObject == sphere || currentObject == tetrahedron) currentObject.setShader(uvTexture);
                        redraw();
                        break;

                    // výběr aktuálního objektu
                    case KeyEvent.VK_T:
                        select(tetrahedron, "čtyřstěn");
                        break;

                    case KeyEvent.VK_V:
                        select(cylinder, "válec");
                        break;

                    case KeyEvent.VK_U:
                        select(sphere, "koule");
                        break;

                    case KeyEvent.VK_K:
                        select(cube, "krychle");
                        break;

                    // rotace
                    case KeyEvent.VK_X: rotateSelected(Math.toRadians(90), new Vec3D(1,0,0)); break;
                    case KeyEvent.VK_Y: rotateSelected(Math.toRadians(90), new Vec3D(0,1,0)); break;
                    case KeyEvent.VK_Z: rotateSelected(Math.toRadians(90), new Vec3D(0,0,1)); break;

                    // translace
                    case KeyEvent.VK_LEFT: moveSelected(-0.1,0); break;
                    case KeyEvent.VK_RIGHT: moveSelected(0.1,0); break;
                    case KeyEvent.VK_UP: moveSelected(0,0.1); break;
                    case KeyEvent.VK_DOWN: moveSelected(0,-0.1); break;

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
    private void moveSelected(double x, double y) {
        transformSelected(new Mat4Transl(x, y, 0));
    }

    // natočení kolem osy podle vektoru
    private void rotateSelected(double angleRad, Vec3D axis) {
        transformSelected(new Mat4Rot(angleRad, axis));
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
        if (currentObject != null) {
            currentObject.setSelected(false);
        }

        currentObject = obj;
        currentObject.setSelected(true);
    }

    // vybere objekt a předá jeho název do canvas
    private void select(Solid obj, String name) {
        setSelectedObject(obj);
        canvas.setCurrentObject(name);
        redraw();
    }

    private void hardClear() {
        canvas.clear();

        for(Solid s : solids) {
            s.setModelMatrix(new Mat4());
        }

        redraw();
    }

    private void redraw() {
        vB.clear();

        initObjects();

        canvas.repaint();
    }
}
