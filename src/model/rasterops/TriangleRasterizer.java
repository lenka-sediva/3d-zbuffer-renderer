package model.rasterops;

import model.rasterdata.Visibility;
import transforms.Col;
import transforms.Point3D;

public class TriangleRasterizer {

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
}