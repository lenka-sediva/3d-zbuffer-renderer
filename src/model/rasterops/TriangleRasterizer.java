package model.rasterops;

import model.rasterdata.Visibility;
import transforms.Col;
import transforms.Point3D;

public class TriangleRasterizer {

    // Prezentace Triangle rasterizer Steflovic - SLIDE 3
    // Prezentace Triangle rasterizer Jezek - OD SLIDE 147
    public static void rasterize(Visibility img, Point3D vA, Point3D vB, Point3D vC, Col color) {

        // převod na double (nezaokrouhlovat hned)
        double aX = vA.getX();
        double aY = vA.getY();
        double aZ = vA.getZ();

        double bX = vB.getX();
        double bY = vB.getY();
        double bZ = vB.getZ();

        double cX = vC.getX();
        double cY = vC.getY();
        double cZ = vC.getZ();

        // seřazení podle y
        if (aY > bY) {
            double tmpX = aX, tmpY = aY, tmpZ = aZ;
            aX = bX; aY = bY; aZ = bZ;
            bX = tmpX; bY = tmpY; bZ = tmpZ;
        }

        if (aY > cY) {
            double tmpX = aX, tmpY = aY, tmpZ = aZ;
            aX = cX; aY = cY; aZ = cZ;
            cX = tmpX; cY = tmpY; cZ = tmpZ;
        }

        if (bY > cY) {
            double tmpX = bX, tmpY = bY, tmpZ = bZ;
            bX = cX; bY = cY; bZ = cZ;
            cX = tmpX; cY = tmpY; cZ = tmpZ;
        }

        int yStart = (int) Math.ceil(aY);
        int yEnd = (int) Math.ceil(cY);

        // horní část trojúhelníka (A -> B)
        for (int y = yStart; y <= (int)Math.ceil(bY); y++) {

            if (bY == aY) break; // ochrana proti dělení nulou

            double t1 = (y - aY) / (bY - aY);
            double t2 = (y - aY) / (cY - aY);

            double x1 = aX + t1 * (bX - aX);
            double z1 = aZ + t1 * (bZ - aZ);

            double x2 = aX + t2 * (cX - aX);
            double z2 = aZ + t2 * (cZ - aZ);

            drawScanline(img, y, x1, z1, x2, z2, color);
        }

        // dolní část trojúhelníka (B → C)
        for (int y = (int)Math.ceil(bY); y <= yEnd; y++) {

            if (cY == bY) break;

            double t1 = (y - bY) / (cY - bY);
            double t2 = (y - aY) / (cY - aY);

            double x1 = bX + t1 * (cX - bX);
            double z1 = bZ + t1 * (cZ - bZ);

            double x2 = aX + t2 * (cX - aX);
            double z2 = aZ + t2 * (cZ - aZ);

            drawScanline(img, y, x1, z1, x2, z2, color);
        }
    }

    private static void drawScanline(Visibility img, int y, double x1, double z1, double x2, double z2, Col color) {

        if (x1 > x2) {
            double tmpX = x1; x1 = x2; x2 = tmpX;
            double tmpZ = z1; z1 = z2; z2 = tmpZ;
        }

        int xStart = (int) Math.ceil(x1);
        int xEnd = (int) Math.ceil(x2);

        for (int x = xStart; x <= xEnd; x++) {

            double t = (x2 == x1) ? 0 : (x - x1) / (x2 - x1);
            double z = z1 + t * (z2 - z1);

            img.setPixelWithZTest(x, y, z, color);
        }
    }
}
