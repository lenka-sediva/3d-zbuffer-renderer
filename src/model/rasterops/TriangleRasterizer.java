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

        // viewport transformation
        Vec2D a = viewportTransformation(vA);
        Vec2D b = viewportTransformation(vB);
        Vec2D c = viewportTransformation(vC);

        // vrcholy
        int aX = (int) Math.round(a.getX());
        int aY = (int) Math.round(a.getY());
        double aZ = vA.getPosition().getZ();

        int bX = (int) Math.round(b.getX());
        int bY = (int) Math.round(b.getY());
        double bZ = vB.getPosition().getZ();

        int cX = (int) Math.round(c.getX());
        int cY = (int) Math.round(c.getY());
        double cZ = vC.getPosition().getZ();

        // Seřazení vrcholů podle Y
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

        // Horní část trojúhelníku
        for (int y = aY; y <= bY; y++) {

            if (bY - aY == 0) continue;
            if (cY - aY == 0) continue;

            double tAB = (double) (y - aY) / (bY - aY);
            double tAC = (double) (y - aY) / (cY - aY);

            int xAB = (int) ((1 - tAB) * aX + tAB * bX);
            double zAB = (1 - tAB) * aZ + tAB * bZ;

            int xAC = (int) ((1 - tAC) * aX + tAC * cX);
            double zAC = (1 - tAC) * aZ + tAC * cZ;

            if (xAB > xAC) {
                int tmpX = xAB; xAB = xAC; xAC = tmpX;
                double tmpZ = zAB; zAB = zAC; zAC = tmpZ;
            }

            for (int x = xAB; x <= xAC; x++) {

                double t = (xAC - xAB) == 0 ? 0 :
                        (double) (x - xAB) / (xAC - xAB);

                double z = (1 - t) * zAB + t * zAC;

                img.setPixelWithZTest(x, y, z, vA.getColor());
            }
        }

        // Spodní část trojúhelníku
        for (int y = bY; y <= cY; y++) {

            if (cY - bY == 0) continue;
            if (cY - aY == 0) continue;

            double tBC = (double) (y - bY) / (cY - bY);
            double tAC = (double) (y - aY) / (cY - aY);

            int xBC = (int) ((1 - tBC) * bX + tBC * cX);
            double zBC = (1 - tBC) * bZ + tBC * cZ;

            int xAC = (int) ((1 - tAC) * aX + tAC * cX);
            double zAC = (1 - tAC) * aZ + tAC * cZ;

            if (xBC > xAC) {
                int tmpX = xBC; xBC = xAC; xAC = tmpX;
                double tmpZ = zBC; zBC = zAC; zAC = tmpZ;
            }

            for (int x = xBC; x <= xAC; x++) {

                double t = (xAC - xBC) == 0 ? 0 :
                        (double) (x - xBC) / (xAC - xBC);

                double z = (1 - t) * zBC + t * zAC;

                img.setPixelWithZTest(x, y, z, vA.getColor());
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
}