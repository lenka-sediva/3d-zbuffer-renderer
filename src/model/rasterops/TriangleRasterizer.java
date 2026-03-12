package model.rasterops;

import model.objectdata.Vertex;
import model.rasterdata.Visibility;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public class TriangleRasterizer {
    private final Visibility img;
    private Function<Vertex, Col> shader;
    private final BufferedImage texture;

    public TriangleRasterizer(Visibility img, Function<Vertex, Col> shader) {
        this.img = img;
        this.shader = shader;
        this.texture = null;
    }

    public void setShader(Function<Vertex, Col> shader) {
        this.shader = shader;
    }

    public void rasterize(Vertex vA, Vertex vB, Vertex vC) {
        double ax = vA.getX(), ay = vA.getY(), az = vA.getZ();
        double bx = vB.getX(), by = vB.getY(), bz = vB.getZ();
        double cx = vC.getX(), cy = vC.getY(), cz = vC.getZ();

        Col colA = vA.getColor();
        Col colB = vB.getColor();
        Col colC = vC.getColor();

        // perspektivně korektní interpolace texturovacích souřadnic:
        // interpolace getTexUV() (= uv/w) a getOne() (= 1/w) zvlášť
        Vec2D uvA = vA.getTexUV();
        Vec2D uvB = vB.getTexUV();
        Vec2D uvC = vC.getTexUV();

        double oneA = vA.getOne();
        double oneB = vB.getOne();
        double oneC = vC.getOne();

        // bounding box oříznutý na rozměry obrazu
        int minX = Math.max((int) Math.floor(Math.min(ax, Math.min(bx, cx))), 0);
        int maxX = Math.min((int) Math.ceil (Math.max(ax, Math.max(bx, cx))), img.getWidth()  - 1);
        int minY = Math.max((int) Math.floor(Math.min(ay, Math.min(by, cy))), 0);
        int maxY = Math.min((int) Math.ceil (Math.max(ay, Math.max(by, cy))), img.getHeight() - 1);

        // znaménková plocha trojúhelníku
        double areaABC = edgeFunction(ax, ay, bx, by, cx, cy);

        // degenerovaný trojúhelník
        if (Math.abs(areaABC) < 1e-10) return;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {

                // střed pixelu pro přesnější testování a interpolaci
                double px = x + 0.5, py = y + 0.5;

                double wA = edgeFunction(bx, by, cx, cy, px, py) / areaABC;
                double wB = edgeFunction(cx, cy, ax, ay, px, py) / areaABC;
                double wC = edgeFunction(ax, ay, bx, by, px, py) / areaABC;

                // pixel musí ležet uvnitř trojúhelníku
                if (wA < 0 || wB < 0 || wC < 0) continue;

                // interpolace Z
                double z = wA * az + wB * bz + wC * cz;

                // lineární interpolace barvy vrcholů
                Col col = colA.mul(wA).add(colB.mul(wB)).add(colC.mul(wC));

                // perspektivně korektní interpolace 1/w a uv/w
                double oneInterp = wA * oneA + wB * oneB + wC * oneC;

                double uInterp = wA * uvA.getX() + wB * uvB.getX() + wC * uvC.getX();
                double vInterp = wA * uvA.getY() + wB * uvB.getY() + wC * uvC.getY();

                // výsledné UV
                double u = oneInterp != 0 ? uInterp / oneInterp : 0;
                double v = oneInterp != 0 ? vInterp / oneInterp : 0;

                // texturování (interní textura rasterizéru)
                if (texture != null) {
                    double uw = u - Math.floor(u); // wrapping
                    double vw = v - Math.floor(v);

                    int tx = (int)(uw * (texture.getWidth()  - 1));
                    int ty = (int)(vw * (texture.getHeight() - 1));

                    int rgb = texture.getRGB(tx, ty);
                    double r = ((rgb >> 16) & 0xFF) / 255.0;
                    double g = ((rgb >>  8) & 0xFF) / 255.0;
                    double b = ((rgb      ) & 0xFF) / 255.0;

                    col = col.mul(new Col(r, g, b));
                }

                // shader dostane vertex s UV
                if (shader != null) {
                    Vertex interpolated = new Vertex(
                            vA.getPosition().mul(wA)
                                    .add(vB.getPosition().mul(wB))
                                    .add(vC.getPosition().mul(wC)),
                            col,
                            new Vec2D(u, v),
                            vA.getNormal().mul(wA).add(vB.getNormal().mul(wB)).add(vC.getNormal().mul(wC)),
                            1.0,
                            new Point3D(
                                    vA.getWorldPos().getX() * wA + vB.getWorldPos().getX() * wB + vC.getWorldPos().getX() * wC,
                                    vA.getWorldPos().getY() * wA + vB.getWorldPos().getY() * wB + vC.getWorldPos().getY() * wC,
                                    vA.getWorldPos().getZ() * wA + vB.getWorldPos().getZ() * wB + vC.getWorldPos().getZ() * wC
                            )
                    );
                    col = shader.apply(interpolated);
                }
                img.setPixelWithZTest(x, y, z, col);
            }
        }
    }

    /**
     * 2D cross product hrany AB vůči bodu P
     * Kladné = P leží vlevo od AB (stejná orientace jako zbytek trojúhelníku)
     */
    private static double edgeFunction(
            double ax, double ay,
            double bx, double by,
            double px, double py) {
        return (bx - ax) * (py - ay) - (by - ay) * (px - ax);
    }
}