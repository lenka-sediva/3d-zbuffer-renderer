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

import java.io.IO;
import java.util.ArrayList;
import java.util.List;

public class Renderer3D {

    private TriangleRasterizer triangleRasterizer;
    private LineRasterizer lineRasterizer;
    private int width;
    private int height;

    public Renderer3D() {

    }

    public void setViewport(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void renderSolids(List<Solid> solids, Mat4 model, Camera view, Mat4 proj) {
        // TODO for each solid
        // render Solid
        if (solids == null) return;

        for (Solid s : solids) {
            renderSolid(s, model, view, proj);
        }
    }

    public void setTriangleRasterizer(TriangleRasterizer triangleRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void renderSolid(Solid solid, Mat4 model, Camera view, Mat4 proj) {
        if(solid == null) return;

        Mat4 mvp = model.mul(view.getViewMatrix()).mul(proj); // model * view * projection

        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = solid.getIndexBuffer();
        List<Part> parts = solid.getPartBuffer();

        // transformace všech Vertexů do clip space
        for(Vertex v : solid.getVertexBuffer()) {
            vertices.add(v.transf(mvp));
        }

        // frustum culling - rychlé ořezání, pokud všechny vrcholy jsou mimo view
        if(isInView(vertices)) {
            for (Part part : parts) {
                switch (part.getTopologyType()) {
                    case LINE_LIST:
                        for (int i = 0; i < part.getCount(); i += 2) {
                            int index1 = indices.get(part.getStartIndex() + i);
                            int index2 = indices.get(part.getStartIndex() + i + 1);

                            Vertex v1 = vertices.get(index1).dehomog().orElse(vertices.get(index1));
                            Vertex v2 = vertices.get(index2).dehomog().orElse(vertices.get(index2));

                            v1 = transformToWindow(v1);
                            v2 = transformToWindow(v2);

                            lineRasterizer.rasterize(v1, v2);
                        }
                        break;

                    case TRIANGLE_LIST:
                        for (int i = 0; i < part.getCount(); i += 3) {
                            int index1 = indices.get(part.getStartIndex() + i);
                            int index2 = indices.get(part.getStartIndex() + i + 1);
                            int index3 = indices.get(part.getStartIndex() + i + 2);

                            clipZ(
                                    vertices.get(index1),
                                    vertices.get(index2),
                                    vertices.get(index3)
                            );
                        }
                        break;

                    case TRIANGLE_STRIP:
                        for (int i = 0; i < part.getCount() - 2; i++) {
                            int index1 = indices.get(part.getStartIndex() + i);
                            int index2 = indices.get(part.getStartIndex() + i + 1);
                            int index3 = indices.get(part.getStartIndex() + i + 2);

                            Vertex v1 = vertices.get(index1);
                            Vertex v2 = vertices.get(index2);
                            Vertex v3 = vertices.get(index3);

                            if (i % 2 == 0) {
                                Vertex temp = v2;
                                v2 = v3;
                                v3 = temp;
                            }
                            clipZ(v1, v2, v3);
                            //start += 3;
                            //triangleRasterizer.rasterize(v1, v2, v3);
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

            if (z >= 0) allFar = false;
            if (z <= w) allBehind = false;
        }

        return !(allLeft || allRight || allUp || allDown || allFar || allBehind);
    }

    // vytvoří nové Vertexy a pošle je do Rasterizeru
    private void clipZ(Vertex vA, Vertex vB, Vertex vC) {
        // protřídění všech Vertexů podle Z
        if (vA.getZ() < vB.getZ()) {
            Vertex temp = vA; vA = vB; vB = temp;
        }

        if(vB.getZ() < vC.getZ()) {
            Vertex temp = vB; vB = vC; vC = temp;
        }

        if(vA.getZ() < vB.getZ()) {
            Vertex temp = vA; vA = vB; vB = temp;
        }

        double zMin = 0.01; // glitch kvůli double nepřesné hodnotě
        // TODO zkusit jinou hodnotu (ne diskrétní), pokud trojúhelník glitchuje
//        IO.println(0.1 + 0.2);
//        IO.println(0.3);

        // ořezání (vA = nejvzdálenější Vertex)
        // 1) trojúhelník je za pozorovatel, nic nevykreslujeme
        if(vA.getZ() < zMin) {
            return;
        }

        // 2) je vidět jen část, kreslíme trojúhelník
        if(vB.getZ() < zMin) {
            double tAB = (zMin - vA.getZ()) / (vB.getZ() - vA.getZ());
            double tAC = (zMin - vA.getZ()) / (vC.getZ() - vA.getZ());

            Vertex vD = Lerp.compute(vA, vB, tAB);
            Vertex vE = Lerp.compute(vA, vC, tAC);

            // vykreslíme nový trojúhelník
            renderTriangle(vA, vD, vE);
            return;
        }

        // 3) je vidět čtyřúhelník, triangulujeme na 2 trojúhelníky
        if(vC.getZ() < zMin) {
            double tBC = (zMin - vB.getZ()) / (vC.getZ() - vB.getZ());
            double tAC = (zMin - vA.getZ()) / (vC.getZ() - vA.getZ());

            // 2 vytvořené trojúhelníky, protože quad
            Vertex vD = Lerp.compute(vB, vC, tBC);
            Vertex vE = Lerp.compute(vA, vC, tAC);

            // vykreslíme nový trojúhelník
            renderTriangle(vA, vB, vD);
            renderTriangle(vA, vD, vE);
            return; // abychom náhodou nevykreslovali ten, co nevidíme
        }

        // 4) je vidět celý trojúhelník
        renderTriangle(vA, vB, vC);
    }

    private void renderTriangle(Vertex v1, Vertex v2, Vertex v3) {
        // dehomog. každý Vertex, abychom byli v NDC
        v1 = v1.dehomog().orElse(v1);
        v2 = v2.dehomog().orElse(v2);
        v3 = v3.dehomog().orElse(v3);

        // transformace do okna
        v1 = transformToWindow(v1);
        v2 = transformToWindow(v2);
        v3 = transformToWindow(v3);;

        // rasterizace (poslat do Rasterizer)
        // 1x triangleRasterizer - pro plochu trojuhelnika
        triangleRasterizer.rasterize(v1, v2, v3);

        // 3x lineRasterizer - pro hrany trojuhelnika
//        lineRasterizer.rasterize(v1, v2);
//        lineRasterizer.rasterize(v2, v3);
//        lineRasterizer.rasterize(v3, v1);
    }

    private Vertex transformToWindow(Vertex v) {
        double x = (v.getX() + 1) * 0.5 * (width - 1);
        double y = (1 - v.getY()) * 0.5 * (height - 1); // Y osa dolů
        double z = v.getZ();

        return new Vertex(
                new Point3D(x, y, z),
                v.getColor()
        );
    }
}
