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
        super("cylinder", "V");
        double halfHeight = height / 2.0;
        double cy = center.getY();

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
                    .orElse(new Vec3D(0, 1, 0));

            // odstíny zelené podle pozice na obvodu
            Col color = new Col(0.0, 0.8, 0.0);

            Point3D posBottom = new Point3D(x, cy - halfHeight, z);
            Point3D posTop = new Point3D(x, cy + halfHeight, z);

            vertexBuffer.add(new Vertex(posBottom, color, new Vec2D(i / (double)segments, 0), normal, 1.0, posBottom));
            vertexBuffer.add(new Vertex(posTop,    color, new Vec2D(i / (double)segments, 1), normal, 1.0, posTop));
        }

        // boční trojúhelníky
        for (int i = 0; i < segments; i++) {
            int bottom1 = i * 2;
            int top1 = bottom1 + 1;
            int bottom2 = bottom1 + 2;
            int top2 = bottom2 + 1;

            int start = indexBuffer.size();
            indexBuffer.add(bottom1); indexBuffer.add(bottom2); indexBuffer.add(top1);
            partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

            start = indexBuffer.size();
            indexBuffer.add(top1); indexBuffer.add(bottom2); indexBuffer.add(top2);
            partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
        }

        // podstavy = střed + obvod, normála nahoru/dolů
        Vec3D nTop = new Vec3D(0,  1, 0);
        Vec3D nBottom = new Vec3D(0, -1, 0);
        Col capCol = new Col(0.0, 0.6, 0.0); // středně zelená

        // index 1. vertexu horní podstavy
        int topCapStart = vertexBuffer.size();

        // střed horní podstavy
        Point3D topCenter = new Point3D(center.getX(), cy + halfHeight, center.getZ());
        vertexBuffer.add(new Vertex(topCenter, capCol, new Vec2D(0.5, 0.5), nTop, 1.0, topCenter));

        // obvod horní podstavy
        for (int i = 0; i <= segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            double x = radius * cos + center.getX();
            double z = radius * sin + center.getZ();
            Point3D pos = new Point3D(x, cy + halfHeight, z);
            Vec2D uv = new Vec2D(cos * 0.5 + 0.5, sin * 0.5 + 0.5);
            vertexBuffer.add(new Vertex(pos, capCol, uv, nTop, 1.0, pos));
        }

        // trojúhelníky horní podstavy
        for (int i = 0; i < segments; i++) {
            int v1 = topCapStart + 1 + i;
            int v2 = topCapStart + 2 + i;
            int start = indexBuffer.size();
            indexBuffer.add(topCapStart); indexBuffer.add(v1); indexBuffer.add(v2);
            partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
        }

        // index 1. vertexu spodní podstavy
        int botCapStart = vertexBuffer.size();

        // střed spodní podstavy
        Point3D botCenter = new Point3D(center.getX(), cy - halfHeight, center.getZ());
        vertexBuffer.add(new Vertex(botCenter, capCol, new Vec2D(0.5, 0.5), nBottom, 1.0, botCenter));

        // obvod spodní podstavy
        for (int i = 0; i <= segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            double x = radius * cos + center.getX();
            double z = radius * sin + center.getZ();
            Point3D pos = new Point3D(x, cy - halfHeight, z);
            Vec2D uv = new Vec2D(cos * 0.5 + 0.5, sin * 0.5 + 0.5);
            vertexBuffer.add(new Vertex(pos, capCol, uv, nBottom, 1.0, pos));
        }

        // trojúhelníky spodní podstavy (obrácené pořadí pro správnou orientaci)
        for (int i = 0; i < segments; i++) {
            int v1 = botCapStart + 1 + i;
            int v2 = botCapStart + 2 + i;
            int start = indexBuffer.size();
            indexBuffer.add(botCapStart); indexBuffer.add(v2); indexBuffer.add(v1);
            partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
        }
    }

    public Cylinder() {
        this(new Point3D(-5, 0, 0), 1.0, 2.0, 20);
    }
}