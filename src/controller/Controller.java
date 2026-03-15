package controller;

import model.objectdata.Solid;
import model.objects.*;
import model.rasterdata.*;
import model.rasterops.*;
import model.renderer.Renderer3D;
import shader.*;
import transforms.*;
import view.Canvas;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Timer;

public class Controller {
    private final Canvas canvas;
    private final Visibility vB;

    private TriangleRasterizer triangleRasterizer;
    private LineRasterizer lineRasterizer;

    private Solid tetrahedron, sphere, cylinder, cube, axis, bicubicSurface;
    private Solid currentObject;
    private List<Solid> solids;

    private final Renderer3D renderer;
    private Camera view;
    private Mat4 persp, ortho, proj;
    private enum ProjectionType { PERSPECTIVE, ORTHOGONAL }
    private ProjectionType currentProjection = ProjectionType.PERSPECTIVE;
    private enum ModeType { SOLID, WIREFRAME }
    private ModeType currentMode = ModeType.SOLID;
    private FragmentShader constantColor, smoothColor, grassTexture, woodTexture, uvTexture;

    // Phong a světlo
    private Light light;
    private Solid lightSphere;
    private Timer lightTimer;
    private static final Point3D lightInitPosition = new Point3D(-2, -1, 2);

    // myš
    private int lastMouseX, lastMouseY;
    private boolean mousePressed = false;

    public Controller(Canvas canvas) {
        this.canvas = canvas;
        canvas.setController(this);
        this.vB = new Visibility(canvas.getRaster());
        canvas.setVisibility(vB); // předání pro resize okna
        this.renderer = new Renderer3D();

        initObjects();
        initListeners(canvas);
        redraw();
    }

    private void initObjects() {
        cube = new Cube();
        cube.setModelMatrix(new Mat4Rot(Math.toRadians(90), new Vec3D(1, 0, 0)));
        tetrahedron = new Tetrahedron();
        sphere = new Sphere();
        cylinder = new Cylinder();
        axis = new Axis();
        bicubicSurface = new BicubicSurface();

        solids = new ArrayList<>();
        solids.add(tetrahedron);
        solids.add(sphere);
        solids.add(cylinder);
        solids.add(cube);
        solids.add(bicubicSurface);

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

        // Phong
        light = new Light(lightInitPosition, new Col(1.0, 1.0, 1.0)); // bílé světlo (barva lze změnit) a bude jí odpovídat i difuzní složka na objektu
        lightSphere = new Sphere(light.getPosition(), 0.25, 10, 10);
        lightSphere.setShader(new ConstantColor(light.getColor())); // koule svítí barvou světla

        canvas.setLightSphere(lightSphere);

        // nastavění Phongova shaderu, aby šlo vidět osvětlení i na textuře
        tetrahedron.setShader(new PhongColor(light));
        sphere.setShader(new PhongColor(light));
        cylinder.setShader(new PhongColor(light));
        cube.setShader(new PhongColor(light));
        bicubicSurface.setShader(new PhongColor(light));
    }

    private void renderScene() {
        vB.clear();

        renderer.setTriangleRasterizer(triangleRasterizer);
        renderer.setLineRasterizer(lineRasterizer);
        renderer.setViewport(canvas.getWidth(), canvas.getHeight());

        Mat4 model = new Mat4Identity();

        persp = new Mat4PerspRH(Math.toRadians(90), (double) canvas.getHeight() / canvas.getWidth(), 0.1, 100); // perspektivní matice projekce
        ortho = new Mat4OrthoRH(canvas.getWidth() / 100., canvas.getHeight() / 100., 0.1, 100); // ortogonální matice projekce

        proj = currentProjection == ProjectionType.PERSPECTIVE ? persp : ortho;

        // renderování všech objektů
        renderer.renderSolids(solids, model, view, proj);
        renderer.renderSolid(axis, model, view, proj);
        renderer.renderSolid(lightSphere, model, view, proj);
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
                        // reset pozice světla (není v solids)
                        lightTimer.stop();
                        light.setPosition(lightInitPosition);
                        lightSphere.setModelMatrix(new Mat4Identity());
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
                        setSurface(constantColor);
                        redraw();
                        break;

                    case KeyEvent.VK_2:
                    case KeyEvent.VK_NUMPAD2:
                        setSurface(smoothColor);
                        redraw();
                        break;

                    case KeyEvent.VK_3:
                    case KeyEvent.VK_NUMPAD3:
                        if (currentObject == cube) setSurface(grassTexture);
                        if (currentObject == cylinder || currentObject == tetrahedron) setSurface(woodTexture);
                        if (currentObject == sphere || currentObject == bicubicSurface) setSurface(uvTexture);
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

                    case KeyEvent.VK_B:
                        select(bicubicSurface, "bicubic");
                        break;

                    case KeyEvent.VK_L:
                        select(lightSphere, "světlo");
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

                    // animace světla v čase
                    case KeyEvent.VK_R:
                        if (lightTimer.isRunning()) {
                            lightTimer.stop();
                        } else {
                            light.startAnimationFromCurrentPosition(); // začátek od aktuální pozice světla
                            lightTimer.start();
                        }
                        break;
                }
            }
        });

        // rozhlížení pomocí myši (kliknutí a tažení)
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                mousePressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!mousePressed) return;

                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                view = view.addAzimuth(dx * 0.005); // citlivost horizontálního pohybu
                view = view.addZenith(-dy * 0.005); // citlivost vertikálního pohybu

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                redraw();
            }
        });

        // ambientní složka osvětlení ze slideru
        canvas.getAmbientSlider().addChangeListener(_ -> {
            double ambient = canvas.getAmbientSlider().getValue() / 100.0;
            forEachPhong(phong -> phong.setAmbient(ambient));
            redraw();
        });

        // spekulární složka
        canvas.getSpecularSlider().addChangeListener(_ -> {
            double specular = canvas.getSpecularSlider().getValue() / 100.0;
            forEachPhong(phong -> phong.setSpecularStrength(specular));
            redraw();
        });

        // difúzní složka
        canvas.getDiffuseSlider().addChangeListener(_ -> {
            double diffuse = canvas.getDiffuseSlider().getValue() / 100.0;
            forEachPhong(phong -> phong.setDiffuseStrength(diffuse));
            redraw();
        });

        // animace světla v čase
        lightTimer = new Timer(16, _ -> { // 60 FPS
            light.animate(0.02);

            Mat4 currentMatrix = lightSphere.getModelMatrix();
            Mat4 rotation = new Mat4Rot(0.02, new Vec3D(0,0,1));
            lightSphere.setModelMatrix(currentMatrix.mul(rotation));

            redraw();
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

        // pokud posouváme světlo, aktualizuj jeho pozici
        if (currentObject == lightSphere) {
            light.setPosition(new Point3D(
                    light.getPosition().getX() + x,
                    light.getPosition().getY() + y,
                    light.getPosition().getZ()
            ));
        }
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

    // pro Phongův shader nastaví povrchový shader (textura nebo barva)
    private void setSurface(FragmentShader surface) {
        if (currentObject != null && currentObject.getShader() instanceof PhongColor) {
            ((PhongColor) currentObject.getShader()).setSurfaceShader(surface);
            redraw();
        }
    }

    private void updateCameraPos() {
        Vec3D eye = view.getEye();
        forEachPhong(phong -> phong.setCameraPos(eye));
    }

    // provede akci pro všechny objekty, které mají Phong shader
    private void forEachPhong(Consumer<PhongColor> action) {
        for (Solid s : solids) {
            if (s.getShader() instanceof PhongColor phong) {
                action.accept(phong);
            }
        }
    }

    private void hardClear() {
        canvas.clear();

        for(Solid s : solids) {
            s.setModelMatrix(new Mat4());
        }

        redraw();
    }

    public void redraw() {
        vB.clear();

        updateCameraPos(); // pro přepočet spekulární složky
        renderScene();

        canvas.repaint();
    }
}
