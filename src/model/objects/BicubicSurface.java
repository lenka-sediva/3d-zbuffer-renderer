package model.objects;

import model.objectdata.*;
import transforms.*;

public class BicubicSurface extends Solid {

    /**
     * @param bicubic instance bikubické plochy
     * @param uCount počet vzorků podél U (horizontálně)
     * @param vCount počet vzorků podél V (vertikálně)
     */
    public BicubicSurface(Bicubic bicubic, int uCount, int vCount) {
        super("bicubic", "B");

        // generování vertexů
        for (int i = 0; i <= vCount; i++) {
            double v = i / (double) vCount;
            for (int j = 0; j <= uCount; j++) {
                double u = j / (double) uCount;

                Point3D p = bicubic.compute(u, v);

                // aproximace normály numericky
                double du = 0.001;
                double dv = 0.001;

                Point3D pu = bicubic.compute(Math.min(u + du, 1.0), v);
                Point3D pv = bicubic.compute(u, Math.min(v + dv, 1.0));

                Vec3D tangentU = pu.ignoreW().sub(p.ignoreW());
                Vec3D tangentV = pv.ignoreW().sub(p.ignoreW());

                Vec3D normal = tangentU.cross(tangentV).normalized().orElse(new Vec3D(0, 0, 1));

                // barva podle normály
                Col color = new Col(
                        (int)((normal.getX() * 0.5 + 0.5) * 255),
                        (int)((normal.getY() * 0.5 + 0.5) * 255),
                        (int)((normal.getZ() * 0.5 + 0.5) * 255)
                );

                Vec2D uv = new Vec2D(u, v);

                vertexBuffer.add(new Vertex(p, color, uv, normal, 1.0));
            }
        }

        // generování indexů pomocí TRIANGLE_STRIP pro každý pás
        for (int i = 0; i < vCount; i++) {
            int start = indexBuffer.size();
            for (int j = 0; j <= uCount; j++) {
                int row1 = i * (uCount + 1) + j;
                int row2 = (i + 1) * (uCount + 1) + j;
                indexBuffer.add(row1);
                indexBuffer.add(row2);
            }
            partBuffer.add(new Part(start, 2 * (uCount + 1), TopologyType.TRIANGLE_STRIP));
        }
    }

    private static Bicubic createBicubic() {
        Point3D[] controlPoints = new Point3D[] {
                new Point3D(-1, -2, 0),
                new Point3D(-0.5, -2, 1),
                new Point3D(0.5, -2, -1),
                new Point3D(1, -2, 0),

                new Point3D(-1, -1.5, 1),
                new Point3D(-0.5, -1.5, 2),
                new Point3D(0.5, -1.5, 0),
                new Point3D(1, -1.5, 1),

                new Point3D(-1, -0.5, -1),
                new Point3D(-0.5, -0.5, 0),
                new Point3D(0.5, -0.5, 2),
                new Point3D(1, -0.5, -1),

                new Point3D(-1, 0, 0),
                new Point3D(-0.5, 0, 1),
                new Point3D(0.5, 0, -1),
                new Point3D(1, 0, 0)
        };

        return new Bicubic(Cubic.BEZIER, controlPoints);
    }

    public BicubicSurface() {
        this(createBicubic(), 20, 20);
    }
}