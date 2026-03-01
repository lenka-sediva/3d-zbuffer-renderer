package model.rasterops;

import model.objectdata.Vertex;
import model.rasterdata.Visibility;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class TriangleRasterizer {

    private Visibility img;

    public TriangleRasterizer(Visibility img) {
        this.img = img;
    }

    // pro žlutý a červený trojúheník se z
    public static void rasterize(Visibility img, Point3D vA, Point3D vB, Point3D vC, Col color) {

        // vrcholy jako double
        double ax = vA.getX();
        double ay = vA.getY();
        double az = vA.getZ();

        double bx = vB.getX();
        double by = vB.getY();
        double bz = vB.getZ();

        double cx = vC.getX();
        double cy = vC.getY();
        double cz = vC.getZ();

        // Bounding box trojúhelníku
        int minX = (int) Math.floor(Math.min(ax, Math.min(bx, cx)));
        int maxX = (int) Math.ceil (Math.max(ax, Math.max(bx, cx)));

        int minY = (int) Math.floor(Math.min(ay, Math.min(by, cy)));
        int maxY = (int) Math.ceil (Math.max(ay, Math.max(by, cy)));

        // Omezíme na velikost bufferu
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, img.getWidth()  - 1);
        maxY = Math.min(maxY, img.getHeight() - 1);

        // Předpočítaný jmenovatel barycentriky
        double denom = (by - cy) * (ax - cx) + (cx - bx) * (ay - cy);

        if (denom == 0.0) return; // degenerovaný trojúhelník

        // Průchod bounding boxem
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {

                // barycentrické váhy
                double px = x + 0.5;
                double py = y + 0.5;

                double wA = ((by - cy) * (px - cx) + (cx - bx) * (py - cy)) / denom;

                double wB = ((cy - ay) * (px - cx) + (ax - cx) * (py - cy)) / denom;

                double wC = 1.0 - wA - wB;

                // inside test
                if ((wA >= 0 && wB >= 0 && wC >= 0) || (wA <= 0 && wB <= 0 && wC <= 0)) {

                    // Interpolace Z (lineární v screen prostoru)
                    double z = wA * az + wB * bz + wC * cz;

                    img.setPixelWithZTest(x, y, z, color);
                }
            }
        }
    }

    // nový

    public void rasterize(Vertex vA, Vertex vB, Vertex vC) {
        // viewport transformace
        Vec2D a = viewportTransformation(vA);
        Vec2D b = viewportTransformation(vB);
        Vec2D c = viewportTransformation(vC);

        // screen souřadnice + Z
        double ax = a.getX(), ay = a.getY(), az = vA.getPosition().getZ();
        double bx = b.getX(), by = b.getY(), bz = vB.getPosition().getZ();
        double cx = c.getX(), cy = c.getY(), cz = vC.getPosition().getZ();

        // bounding box
        int minX = (int)Math.floor(Math.min(ax, Math.min(bx, cx)));
        int maxX = (int)Math.ceil (Math.max(ax, Math.max(bx, cx)));
        int minY = (int)Math.floor(Math.min(ay, Math.min(by, cy)));
        int maxY = (int)Math.ceil (Math.max(ay, Math.max(by, cy)));

        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, img.getWidth() - 1);
        maxY = Math.min(maxY, img.getHeight() - 1);

        // předpočítaný jmenovatel pro barycentriku
        double denom = (by - cy)*(ax - cx) + (cx - bx)*(ay - cy);
        if (denom == 0.0) return; // degenerovaný trojúhelník

        // průchod bounding boxem
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double px = x + 0.5;
                double py = y + 0.5;

                // barycentrické váhy
                double wA = ((by - cy)*(px - cx) + (cx - bx)*(py - cy)) / denom;
                double wB = ((cy - ay)*(px - cx) + (ax - cx)*(py - cy)) / denom;
                double wC = 1.0 - wA - wB;

                // inside test
                if ((wA >= 0 && wB >= 0 && wC >= 0) || (wA <= 0 && wB <= 0 && wC <= 0)) {

                    // interpolace Z
                    double z = wA * az + wB * bz + wC * cz;

                    // interpolace barvy
                    Col color = interpolateColor(vA, vB, vC, wA, wB, wC);
                    img.setPixelWithZTest(x, y, z, color);
                }
            }
        }
    }

    private Vec2D viewportTransformation(Vertex v) {
        int width = img.getWidth();
        int height = img.getHeight();

        double xNdc = v.getPosition().getX();
        double yNdc = v.getPosition().getY();

        double xScreen = (xNdc + 1.0) * 0.5 * width;
        double yScreen = (1.0 - yNdc) * 0.5 * height;

        return new Vec2D(xScreen, yScreen);
    }

    private Col interpolateColor(Vertex vA, Vertex vB, Vertex vC, double wA, double wB, double wC) {
        double r = wA * vA.getColor().getR() + wB * vB.getColor().getR() + wC * vC.getColor().getR();
        double g = wA * vA.getColor().getG() + wB * vB.getColor().getG() + wC * vC.getColor().getG();
        double b = wA * vA.getColor().getB() + wB * vB.getColor().getB() + wC * vC.getColor().getB();

        return new Col(r, g, b);
    }
}