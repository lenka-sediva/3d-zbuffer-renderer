package model.rasterops;

import model.rasterdata.Visibility;
import transforms.Col;
import transforms.Point3D;

public class TriangleRasterizer {

    public static void rasterize(Visibility img, Point3D vA, Point3D vB, Point3D vC, Col color) {

        // Souřadnice X a Y jako int pro rasterizaci, Z jako double pro přesný Z-test
        int aX = (int) Math.round(vA.getX());
        int aY = (int) Math.round(vA.getY());
        double aZ = vA.getZ(); // <-- double, NEzaokrouhlujeme!

        int bX = (int) Math.round(vB.getX());
        int bY = (int) Math.round(vB.getY());
        double bZ = vB.getZ();

        int cX = (int) Math.round(vC.getX());
        int cY = (int) Math.round(vC.getY());
        double cZ = vC.getZ();

        // ===== 1) Seřazení vrcholů podle Y (aY <= bY <= cY) =====
        if (aY > bY) {
            int tmpX = aX; aX = bX; bX = tmpX;
            int tmpY = aY; aY = bY; bY = tmpY;
            double tmpZ = aZ; aZ = bZ; bZ = tmpZ;
        }
        if (bY > cY) {
            int tmpX = bX; bX = cX; cX = tmpX;
            int tmpY = bY; bY = cY; cY = tmpY;
            double tmpZ = bZ; bZ = cZ; cZ = tmpZ;
        }
        if (aY > bY) {
            int tmpX = aX; aX = bX; bX = tmpX;
            int tmpY = aY; aY = bY; bY = tmpY;
            double tmpZ = aZ; aZ = bZ; bZ = tmpZ;
        }

        // Jmenovatel pro barycentrické souřadnice – platný pro celý trojúhelník
        double denom = (double)((bY - cY) * (aX - cX) + (cX - bX) * (aY - cY));

        // Degenerovaný trojúhelník (všechny vrcholy na jedné přímce)
        if (denom == 0) return;

        // ===== 2) Horní část trojúhelníku (aY -> bY) =====
        for (int y = aY; y <= bY; y++) {

            if (bY - aY == 0) continue;
            if (cY - aY == 0) continue;

            double tAB = (double)(y - aY) / (bY - aY);
            int xAB = (int)((1 - tAB) * aX + tAB * bX);

            double tAC = (double)(y - aY) / (cY - aY);
            int xAC = (int)((1 - tAC) * aX + tAC * cX);

            int xLeft  = Math.min(xAB, xAC);
            int xRight = Math.max(xAB, xAC);

            for (int x = xLeft; x <= xRight; x++) {
                double z = interpolateZ(x, y, aX, aY, aZ, bX, bY, bZ, cX, cY, cZ, denom);
                img.setPixelWithZTest(x, y, z, color);
            }
        }

        // ===== 3) Spodní část trojúhelníku (bY -> cY) =====
        for (int y = bY; y <= cY; y++) {

            if (cY - bY == 0) continue;
            if (cY - aY == 0) continue;

            double tBC = (double)(y - bY) / (cY - bY);
            int xBC = (int)((1 - tBC) * bX + tBC * cX);

            double tAC = (double)(y - aY) / (cY - aY);
            int xAC = (int)((1 - tAC) * aX + tAC * cX);

            int xLeft  = Math.min(xBC, xAC);
            int xRight = Math.max(xBC, xAC);

            for (int x = xLeft; x <= xRight; x++) {
                double z = interpolateZ(x, y, aX, aY, aZ, bX, bY, bZ, cX, cY, cZ, denom);
                img.setPixelWithZTest(x, y, z, color);
            }
        }
    }

    /**
     * Vypočítá Z hodnotu pixelu (x, y) pomocí barycentrických souřadnic.
     * Tato metoda je přesná a nevnáší chyby zaokrouhlování jako dřívější
     * lineární interpolace přes zaokrouhlené hrany.
     */
    private static double interpolateZ(
            int x, int y,
            int aX, int aY, double aZ,
            int bX, int bY, double bZ,
            int cX, int cY, double cZ,
            double denom) {

        double wA = ((bY - cY) * (x - cX) + (cX - bX) * (y - cY)) / denom;
        double wB = ((cY - aY) * (x - cX) + (aX - cX) * (y - cY)) / denom;
        double wC = 1.0 - wA - wB;

        return wA * aZ + wB * bZ + wC * cZ;
    }
}