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

            // odstíny zelené podle pozice na obvodu
            double shade = cos * 0.5 + 0.5; // 0.0 - 1.0 podle úhlu
            Col color = new Col(0.0, 0.2 + shade * 0.8, 0.0); // tmavě až světle zelená

            // spodní a vrchní vertex pro segment
            Point3D posBottom = new Point3D(x, center.getY() - halfHeight, z);
            Point3D posTop = new Point3D(x, center.getY() + halfHeight, z);

            vertexBuffer.add(new Vertex(posBottom, color, new Vec2D(i / (double)segments, 0), normal, 1.0, posBottom));
            vertexBuffer.add(new Vertex(posTop, color, new Vec2D(i / (double)segments, 1), normal, 1.0, posTop));
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