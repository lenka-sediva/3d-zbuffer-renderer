package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Sphere extends Solid {

    public Sphere(Point3D center, double radius, int stacks, int slices) {

        // generování vrcholů
        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI * i / stacks;

            for (int j = 0; j <= slices; j++) {
                double theta = 2 * Math.PI * j / slices;

                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.sin(phi) * Math.sin(theta);
                double z = radius * Math.cos(phi);

                Point3D p = new Point3D(
                        center.getX() + x,
                        center.getY() + y,
                        center.getZ() + z
                );

                vertexBuffer.add(new Vertex(p, new Col(255,255,255)));
            }
        }

        // generování indexů
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {

                int first = i * (slices + 1) + j;
                int second = first + slices + 1;

                indexBuffer.add(first);
                indexBuffer.add(second);
                indexBuffer.add(first + 1);

                indexBuffer.add(second);
                indexBuffer.add(second + 1);
                indexBuffer.add(first + 1);
            }
        }

        partBuffer.add(new Part(0, indexBuffer.size(), TopologyType.TRIANGLE_LIST));
    }
}