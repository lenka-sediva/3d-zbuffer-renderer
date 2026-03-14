package model.renderer;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.Vertex;
import model.rasterops.LineRasterizer;
import model.rasterops.TriangleRasterizer;
import transforms.Camera;
import transforms.Mat4;
import transforms.Point3D;
import utilities.Lerp;

import java.util.ArrayList;
import java.util.List;

public class Renderer3D {

    private TriangleRasterizer triangleRasterizer;
    private LineRasterizer lineRasterizer;
    private int width;
    private int height;
    private boolean wireframe = false;

    public Renderer3D() {}

    public void setViewport(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public void setTriangleRasterizer(TriangleRasterizer triangleRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void renderSolids(List<Solid> solids, Mat4 model, Camera view, Mat4 proj) {
        if (solids == null) return;
        for (Solid s : solids) {
            renderSolid(s, model, view, proj);
        }
    }

    public void renderSolid(Solid solid, Mat4 model, Camera view, Mat4 proj) {
        if (solid == null) return;

        // nastavení shaderu podle objektu
        if (solid.getShader() != null) {
            triangleRasterizer.setShader(solid.getShader()::getColor);
        } else {
            triangleRasterizer.setShader(v -> v.getColor()); // fallback = barva vrcholu
        }

        Mat4 modelMatrix = solid.getModelMatrix();
        Mat4 mvp = modelMatrix.mul(view.getViewMatrix()).mul(proj); // model * view * projection

        List<Vertex> verticesClip  = new ArrayList<>(); // clip space = pro rasterizaci a Z-buffer
        List<Vertex> verticesWorld = new ArrayList<>(); // world space = pro Phong (normály, pozice)

        for (Vertex v : solid.getVertexBuffer()) {
            verticesClip.add(v.transf(mvp));         // pozice do clip space
            verticesWorld.add(v.transf(modelMatrix)); // pozice + normála do world space
        }

        List<Integer> indices = solid.getIndexBuffer();
        List<Part> parts = solid.getPartBuffer();

        // frustum culling = rychlé ořezání, pokud všechny vrcholy jsou mimo view
        if (isInView(verticesClip)) {
            for (Part part : parts) {
                switch (part.getTopologyType()) {
                    case LINE_LIST:
                        for (int i = 0; i < part.getCount(); i += 2) {
                            int i1 = indices.get(part.getStartIndex() + i);
                            int i2 = indices.get(part.getStartIndex() + i + 1);

                            Vertex v1 = verticesClip.get(i1).dehomog().orElse(verticesClip.get(i1));
                            Vertex v2 = verticesClip.get(i2).dehomog().orElse(verticesClip.get(i2));

                            clipLineZ(v1, v2);
                        }
                        break;

                    case TRIANGLE_LIST:
                        for (int i = 0; i < part.getCount(); i += 3) {
                            int i1 = indices.get(part.getStartIndex() + i);
                            int i2 = indices.get(part.getStartIndex() + i + 1);
                            int i3 = indices.get(part.getStartIndex() + i + 2);

                            clipZ(
                                    verticesClip.get(i1),  verticesClip.get(i2),  verticesClip.get(i3),
                                    verticesWorld.get(i1), verticesWorld.get(i2), verticesWorld.get(i3)
                            );
                        }
                        break;

                    case TRIANGLE_STRIP:
                        for (int i = 0; i < part.getCount() - 2; i++) {
                            int i1 = indices.get(part.getStartIndex() + i);
                            int i2 = indices.get(part.getStartIndex() + i + 1);
                            int i3 = indices.get(part.getStartIndex() + i + 2);

                            Vertex c1 = verticesClip.get(i1);
                            Vertex c2 = verticesClip.get(i2);
                            Vertex c3 = verticesClip.get(i3);

                            Vertex w1 = verticesWorld.get(i1);
                            Vertex w2 = verticesWorld.get(i2);
                            Vertex w3 = verticesWorld.get(i3);

                            // střídání pořadí vrcholů pro správnou orientaci
                            if (i % 2 == 0) {
                                Vertex tmp = c2; c2 = c3; c3 = tmp;
                                Vertex tmpW = w2; w2 = w3; w3 = tmpW;
                            }

                            clipZ(c1, c2, c3, w1, w2, w3);
                        }
                        break;
                }
            }
        }
    }

    // rychlé ořezání
    private boolean isInView(List<Vertex> vertices) {
        boolean allLeft = true;
        boolean allRight = true;
        boolean allUp = true;
        boolean allDown = true;
        boolean allFar = true;
        boolean allBehind = true;

        for (Vertex v : vertices) {
            double x = v.getPosition().getX();
            double y = v.getPosition().getY();
            double z = v.getPosition().getZ();
            double w = v.getPosition().getW();

            if (x >= -w) allLeft = false;
            if (x <=  w) allRight = false;
            if (y >= -w) allUp = false;
            if (y <=  w) allDown = false;
            if (z >= 0)  allFar = false;
            if (z <= w)  allBehind = false;
        }

        return !(allLeft || allRight || allUp || allDown || allFar || allBehind);
    }

    // ořezání podle near plane, propaguje world space vertexy dál
    private void clipZ(Vertex cA, Vertex cB, Vertex cC,
                       Vertex wA, Vertex wB, Vertex wC) {
        // seřazení podle Z (clip space)
        if (cA.getZ() < cB.getZ()) { Vertex t = cA; cA = cB; cB = t; t = wA; wA = wB; wB = t; }
        if (cB.getZ() < cC.getZ()) { Vertex t = cB; cB = cC; cC = t; t = wB; wB = wC; wC = t; }
        if (cA.getZ() < cB.getZ()) { Vertex t = cA; cA = cB; cB = t; t = wA; wA = wB; wB = t; }

        double zMin = 0.01;

        // 1) celý trojúhelník za pozorovatelem
        if (cA.getZ() < zMin) return;

        // 2) viditelný jen 1 vrchol => 1 trojúhelník
        if (cB.getZ() < zMin) {
            double tAB = (zMin - cA.getZ()) / (cB.getZ() - cA.getZ());
            double tAC = (zMin - cA.getZ()) / (cC.getZ() - cA.getZ());

            Vertex cD = Lerp.compute(cA, cB, tAB);
            Vertex cE = Lerp.compute(cA, cC, tAC);
            Vertex wD = Lerp.compute(wA, wB, tAB);
            Vertex wE = Lerp.compute(wA, wC, tAC);

            renderTriangle(cA, cD, cE, wA, wD, wE);
            return;
        }

        // 3) viditelný quad => 2 trojúhelníky
        if (cC.getZ() < zMin) {
            double tBC = (zMin - cB.getZ()) / (cC.getZ() - cB.getZ());
            double tAC = (zMin - cA.getZ()) / (cC.getZ() - cA.getZ());

            Vertex cD = Lerp.compute(cB, cC, tBC);
            Vertex cE = Lerp.compute(cA, cC, tAC);
            Vertex wD = Lerp.compute(wB, wC, tBC);
            Vertex wE = Lerp.compute(wA, wC, tAC);

            renderTriangle(cA, cB, cD, wA, wB, wD);
            renderTriangle(cA, cD, cE, wA, wD, wE);
            return;
        }

        // 4) celý trojúhelník viditelný
        renderTriangle(cA, cB, cC, wA, wB, wC);
    }

    // ořezání pro úsečky
    private void clipLineZ(Vertex a, Vertex b) {
        double zMin = 0.01;

        // oba body za kamerou
        if (a.getZ() < zMin && b.getZ() < zMin) return;

        // 1 bod za kamerou
        if (a.getZ() < zMin || b.getZ() < zMin) {
            Vertex near = a;
            Vertex far = b;

            if (a.getZ() < zMin) {
                near = b;
                far = a;
            }

            double t = (zMin - near.getZ()) / (far.getZ() - near.getZ());

            Vertex intersection = Lerp.compute(near, far, t);

            if (a.getZ() < zMin) a = intersection;
            else b = intersection;
        }

        a = a.dehomog().orElse(a);
        b = b.dehomog().orElse(b);

        a = transformToWindow(a);
        b = transformToWindow(b);

        lineRasterizer.rasterize(a, b);
    }


    private void renderTriangle(Vertex c1, Vertex c2, Vertex c3,
                                Vertex w1, Vertex w2, Vertex w3) {
        // dehomogenizace do NDC
        c1 = c1.dehomog().orElse(c1);
        c2 = c2.dehomog().orElse(c2);
        c3 = c3.dehomog().orElse(c3);

        // transformace do window space
        c1 = transformToWindow(c1);
        c2 = transformToWindow(c2);
        c3 = transformToWindow(c3);

        // smíšený vertex: screen pozice + barva + UV z clip space, normála + world pozice z world space pro Phong
        Vertex r1 = new Vertex(c1.getPosition(), c1.getColor(), c1.getTexUV(), w1.getNormal(), c1.getOne(),  w1.getPosition());
        Vertex r2 = new Vertex(c2.getPosition(), c2.getColor(), c2.getTexUV(), w2.getNormal(), c2.getOne(),  w2.getPosition());
        Vertex r3 = new Vertex(c3.getPosition(), c3.getColor(), c3.getTexUV(), w3.getNormal(), c3.getOne(),  w3.getPosition());

        if (wireframe) {
            // drátový model = pouze hrany trojúhelníka
            lineRasterizer.rasterize(r1, r2);
            lineRasterizer.rasterize(r2, r3);
            lineRasterizer.rasterize(r3, r1);
        } else {
            // vyplněný model = plocha trojúhelníka
            triangleRasterizer.rasterize(r1, r2, r3);
        }
    }

    private Vertex transformToWindow(Vertex v) {
        double x = (v.getX() + 1) * 0.5 * (width - 1);
        double y = (1 - v.getY()) * 0.5 * (height - 1); // Y osa dolů
        double z = v.getZ();

        return new Vertex(
                new Point3D(x, y, z),
                v.getColor(),
                v.getTexUV(),
                v.getNormal(),
                v.getOne(),
                v.getWorldPos()
        );
    }
}