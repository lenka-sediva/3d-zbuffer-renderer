package model.objects;

import model.objectdata.*;
import transforms.*;

public class Cylinder extends Solid {

    /**
     * @param center střed válce
     * @param radius poloměr
     * @param height výška
     * @param segments počet segmentů pro hladký vzhled (čím více, tím hladší)
     */
    public Cylinder(Point3D center, double radius, double height, int segments) {
        super("válec", "V");
        double halfHeight = height / 2.0;

        // vertexy boční stěny
        for (int i = 0; i <= segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);

            double x = radius * cos + center.getX();
            double z = radius * sin + center.getZ();

            // normála pro boční vertexy
            Vec3D normal = new Vec3D(cos, 0, sin)
                    .normalized()
                    .orElse(new Vec3D(0, 1, 0)); // fallback

            // interpolace barvy podle normály
            Col color = new Col(
                    (int)((normal.getX() * 0.5 + 0.5) * 255),
                    (int)((normal.getY() * 0.5 + 0.5) * 255),
                    (int)((normal.getZ() * 0.5 + 0.5) * 255)
            );

            // spodní a vrchní vertex pro segment
            vertexBuffer.add(new Vertex(
                    new Point3D(x, center.getY() - halfHeight, z),
                    color, new Vec2D(i / (double)segments, 0), normal, 1.0
            ));

            vertexBuffer.add(new Vertex(
                    new Point3D(x, center.getY() + halfHeight, z),
                    color, new Vec2D(i / (double)segments, 1), normal, 1.0
            ));
        }

        // vytvoření indexů a partů
        for (int i = 0; i < segments; i++) {
            int bottom1 = i * 2;
            int top1 = bottom1 + 1;
            int bottom2 = bottom1 + 2;
            int top2 = bottom2 + 1;

            // 1. trojúhelník
            int start = indexBuffer.size();
            indexBuffer.add(bottom1);
            indexBuffer.add(bottom2);
            indexBuffer.add(top1);
            partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

            // 2. trojúhelník
            start = indexBuffer.size();
            indexBuffer.add(top1);
            indexBuffer.add(bottom2);
            indexBuffer.add(top2);
            partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
        }
    }
}