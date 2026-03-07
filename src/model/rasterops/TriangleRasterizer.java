package model.rasterops;

import model.objectdata.Vertex;
import model.rasterdata.Visibility;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class TriangleRasterizer {

    private final Visibility img;

    public TriangleRasterizer(Visibility img) {
        this.img = img;
    }

    // Model -> Rasterizer
    // pro žlutý a červený trojúheník se Z
    // rasterizace s konstantní barvou ve screen prostoru
    public static void rasterize(Visibility img, Point3D vA, Point3D vB, Point3D vC, Col color) {
        double ax = vA.getX();
        double ay = vA.getY();
        double az = vA.getZ();

        double bx = vB.getX();
        double by = vB.getY();
        double bz = vB.getZ();

        double cx = vC.getX();
        double cy = vC.getY();
        double cz = vC.getZ();

        // seřazení podle Y
        if (ay > by) {
            double tx = ax; ax = bx; bx = tx;
            double ty = ay; ay = by; by = ty;
            double tz = az; az = bz; bz = tz;
        }

        if (by > cy) {
            double tx = bx; bx = cx; cx = tx;
            double ty = by; by = cy; cy = ty;
            double tz = bz; bz = cz; cz = tz;
        }

        if (ay > by) {
            double tx = ax; ax = bx; bx = tx;
            double ty = ay; ay = by; by = ty;
            double tz = az; az = bz; bz = tz;
        }

        // výpočet rozsahu Y
        int yStart = (int) Math.ceil(ay); // horní vrchol
        int yMiddle = (int) Math.ceil(by); // střední
        int yEnd = (int) Math.ceil(cy); // spodní vrchol

        // horní část
        for (int y = yStart; y < yMiddle; y++) {
            // lineární interpolace podél hran trojúhelníku
            double tAB = (by - ay) != 0 ? (y - ay) / (by - ay) : 0;
            double tAC = (cy - ay) != 0 ? (y - ay) / (cy - ay) : 0;

            // výpočet pozic X a Z bodů na hranách pro aktuální Y
            double xAB = ax + tAB * (bx - ax);
            double zAB = az + tAB * (bz - az);

            double xAC = ax + tAC * (cx - ax);
            double zAC = az + tAC * (cz - az);

            // xAB = levá hrana, xAC = pravá hrana
            if (xAB > xAC) {
                double tx = xAB; xAB = xAC; xAC = tx;
                double tz = zAB; zAB = zAC; zAC = tz;
            }

            // pro každý pixel na řádku interpoluje Z a kreslí pixel s testem hloubky
            for (int x = (int) Math.ceil(xAB); x <= (int) Math.floor(xAC); x++) {
                if (Math.abs(xAC - xAB) < 1e-6) {
                    img.setPixelWithZTest(x, y, zAB, color);
                    continue;
                }

                double t = (x - xAB) / (xAC - xAB);
                double z = zAB + t * (zAC - zAB);

                img.setPixelWithZTest(x, y, z, color);
            }
        }

        // dolní část
        for (int y = yMiddle; y <= yEnd; y++) {
            // interpolace po hranách BC a AC
            double tBC = (cy - by) != 0 ? (y - by) / (cy - by) : 0;
            double tAC = (cy - ay) != 0 ? (y - ay) / (cy - ay) : 0;

            double xBC = bx + tBC * (cx - bx);
            double zBC = bz + tBC * (cz - bz);

            double xAC = ax + tAC * (cx - ax);
            double zAC = az + tAC * (cz - az);

            if (xBC > xAC) {
                double tx = xBC; xBC = xAC; xAC = tx;
                double tz = zBC; zBC = zAC; zAC = tz;
            }

            for (int x = (int) Math.ceil(xBC); x <= (int) Math.floor(xAC); x++) {
                if (Math.abs(xAC - xBC) < 1e-6) {
                    img.setPixelWithZTest(x, y, zBC, color);
                    continue;
                }

                double t = (x - xBC) / (xAC - xBC);
                double z = zBC + t * (zAC - zBC);

                img.setPixelWithZTest(x, y, z, color);
            }
        }
    }

    // Model -> Renderer -> Rasterizer
    // pro šipku
    // součást 3D pipeline, provádí viewport transformaci a Gouradovu interpolaci atributů
    public void rasterize(Vertex vA, Vertex vB, Vertex vC) {
        // viewport transformace
//        Vec2D a = viewportTransformation(vA);
//        Vec2D b = viewportTransformation(vB);
//        Vec2D c = viewportTransformation(vC);

        double ax = vA.getX(), ay = vA.getY(), az = vA.getPosition().getZ();
        double bx = vB.getX(), by = vB.getY(), bz = vB.getPosition().getZ();
        double cx = vC.getX(), cy = vC.getY(), cz = vC.getPosition().getZ();

        Col colA = vA.getColor();
        Col colB = vB.getColor();
        Col colC = vC.getColor();

        // seřazení podle Y
        if (ay > by) {
            double tmpX = ax; ax = bx; bx = tmpX;
            double tmpY = ay; ay = by; by = tmpY;
            double tmpZ = az; az = bz; bz = tmpZ;
            Col tmpCol = colA; colA = colB; colB = tmpCol;
        }

        if (by > cy) {
            double tmpX = bx; bx = cx; cx = tmpX;
            double tmpY = by; by = cy; cy = tmpY;
            double tmpZ = bz; bz = cz; cz = tmpZ;
            Col tmpCol = colB; colB = colC; colC = tmpCol;
        }

        if (ay > by) {
            double tmpX = ax; ax = bx; bx = tmpX;
            double tmpY = ay; ay = by; by = tmpY;
            double tmpZ = az; az = bz; bz = tmpZ;
            Col tmpCol = colA; colA = colB; colB = tmpCol;
        }

        int yStart = (int) Math.ceil(ay);
        int yMiddle = (int) Math.ceil(by);
        int yEnd = (int) Math.ceil(cy);

        // horní část
        for (int y = yStart; y < yMiddle; y++) {
            // váhové koeficienty pro lineární interpolaci = jak daleko se nacházíme mezi 2 body
            double tAB = (by - ay) != 0 ? (y - ay) / (by - ay) : 0;
            double tAC = (cy - ay) != 0 ? (y - ay) / (cy - ay) : 0;

            double xAB = ax + tAB * (bx - ax);
            double zAB = az + tAB * (bz - az);
            Col colAB = colA.mul(1 - tAB).add(colB.mul(tAB)); // interpolovaná barva po vertikálních hranách

            double xAC = ax + tAC * (cx - ax);
            double zAC = az + tAC * (cz - az);
            Col colAC = colA.mul(1 - tAC).add(colC.mul(tAC));

            if (xAB > xAC) {
                double tmpX = xAB; xAB = xAC; xAC = tmpX;
                double tmpZ = zAB; zAB = zAC; zAC = tmpZ;
                Col tmpCol = colAB; colAB = colAC; colAC = tmpCol;
            }

            for (int x = (int)Math.ceil(xAB); x <= (int)Math.floor(xAC); x++) {
                double t = (xAC - xAB) != 0 ? (x - xAB) / (xAC - xAB) : 0;

                double z = zAB + t * (zAC - zAB);
                Col col = colAB.mul(1 - t).add(colAC.mul(t));

                img.setPixelWithZTest(x, y, z, col);
            }
        }

        // dolní část
        for (int y = yMiddle; y <= yEnd; y++) {
            double tBC = (cy - by) != 0 ? (y - by) / (cy - by) : 0;
            double tAC = (cy - ay) != 0 ? (y - ay) / (cy - ay) : 0;

            double xBC = bx + tBC * (cx - bx);
            double zBC = bz + tBC * (cz - bz);
            Col colBC = colB.mul(1 - tBC).add(colC.mul(tBC));

            double xAC = ax + tAC * (cx - ax);
            double zAC = az + tAC * (cz - az);
            Col colAC = colA.mul(1 - tAC).add(colC.mul(tAC));

            if (xBC > xAC) {
                double tmpX = xBC; xBC = xAC; xAC = tmpX;
                double tmpZ = zBC; zBC = zAC; zAC = tmpZ;
                Col tmpCol = colBC; colBC = colAC; colAC = tmpCol;
            }

            for (int x = (int)Math.ceil(xBC); x <= (int)Math.floor(xAC); x++) {
                double t = (xAC - xBC) != 0 ? (x - xBC) / (xAC - xBC) : 0;

                double z = zBC + t * (zAC - zBC);
                Col col = colBC.mul(1 - t).add(colAC.mul(t));

                img.setPixelWithZTest(x, y, z, col);
            }
        }
    }

    // převádí NDC souřadnice (-1 až 1) do pixelů obrazovky
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