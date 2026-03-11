package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Cylinder extends Solid {

    public Cylinder(Point3D center, double radius, double height, int segments) {

        int topCenterIndex;
        int bottomCenterIndex;

        double halfH = height / 2.0;

        // horní a dolní vrcholy kruhu
        for (int i = 0; i < segments; i++) {

            double angle = 2 * Math.PI * i / segments;

            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);

            // horní kruh
            vertexBuffer.add(new Vertex(
                    new Point3D(center.getX() + x, center.getY() + y, center.getZ() + halfH),
                    new Col(200,200,200)
            ));

            // dolní kruh
            vertexBuffer.add(new Vertex(
                    new Point3D(center.getX() + x, center.getY() + y, center.getZ() - halfH),
                    new Col(150,150,150)
            ));
        }

        // středy kruhů
        topCenterIndex = vertexBuffer.size();
        vertexBuffer.add(new Vertex(
                new Point3D(center.getX(), center.getY(), center.getZ() + halfH),
                new Col(255,0,0)
        ));

        bottomCenterIndex = vertexBuffer.size();
        vertexBuffer.add(new Vertex(
                new Point3D(center.getX(), center.getY(), center.getZ() - halfH),
                new Col(0,255,0)
        ));

        int start = indexBuffer.size();

        // boční plášť
        for (int i = 0; i < segments; i++) {

            int top1 = i * 2;
            int bottom1 = i * 2 + 1;

            int top2 = ( (i + 1) % segments ) * 2;
            int bottom2 = ( (i + 1) % segments ) * 2 + 1;

            // trojúhelník 1
            indexBuffer.add(top1);
            indexBuffer.add(bottom1);
            indexBuffer.add(top2);

            // trojúhelník 2
            indexBuffer.add(top2);
            indexBuffer.add(bottom1);
            indexBuffer.add(bottom2);
        }

        // horní podstava
        for (int i = 0; i < segments; i++) {

            int v1 = i * 2;
            int v2 = ((i + 1) % segments) * 2;

            indexBuffer.add(topCenterIndex);
            indexBuffer.add(v1);
            indexBuffer.add(v2);
        }

        // dolní podstava
        for (int i = 0; i < segments; i++) {

            int v1 = i * 2 + 1;
            int v2 = ((i + 1) % segments) * 2 + 1;

            indexBuffer.add(bottomCenterIndex);
            indexBuffer.add(v2);
            indexBuffer.add(v1);
        }

        partBuffer.add(new Part(start, indexBuffer.size() - start, TopologyType.TRIANGLE_LIST));
    }
}