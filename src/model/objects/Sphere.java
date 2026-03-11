package model.objects;

import model.objectdata.*;
import transforms.*;

public class Sphere extends Solid {

    /**
     * Vytvoří barevnou kouli s normálami a UV
     *
     * @param center střed koule
     * @param radius poloměr
     * @param stacks počet horizontálních vrstev
     * @param slices počet vertikálních vrstev
     */
    public Sphere(Point3D center, double radius, int stacks, int slices) {

        // generování vrcholů
        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI * i / stacks; // úhel od vrcholu ke spodku

            for (int j = 0; j <= slices; j++) {
                double theta = 2 * Math.PI * j / slices; // úhel kolem Y osy

                // souřadnice koule v lokálním prostoru
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.sin(phi) * Math.sin(theta);
                double z = radius * Math.cos(phi);

                // vytvoření pozice ve světových souřadnicích
                Point3D position = new Point3D(
                        center.getX() + x,
                        center.getY() + y,
                        center.getZ() + z
                );

                // normála (pro interpolaci barvy)
                Vec3D normal = new Vec3D(x, y, z).normalized()
                        .orElse(new Vec3D(0, 0, 1)); // fallback pro případ nulové délky

                // barva založená na normále (RGB = interpolace z -1..1 na 0..255)
                Col color = new Col(
                        (int)((normal.getX() * 0.5 + 0.5) * 255),
                        (int)((normal.getY() * 0.5 + 0.5) * 255),
                        (int)((normal.getZ() * 0.5 + 0.5) * 255)
                );

                // UV souřadnice pro případ textury
                Vec2D uv = new Vec2D(j / (double) slices, i / (double) stacks);

                // přidání vertexu do bufferu
                vertexBuffer.add(new Vertex(position, color, uv, normal, 1.0));
            }
        }

        // generování indexů pro trojúhelníky
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int current = i * (slices + 1) + j;
                int next = current + slices + 1;

                // první trojúhelník quadu
                int start = indexBuffer.size();
                indexBuffer.add(current);
                indexBuffer.add(next);
                indexBuffer.add(current + 1);
                partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

                // druhý trojúhelník quadu
                start = indexBuffer.size();
                indexBuffer.add(next);
                indexBuffer.add(next + 1);
                indexBuffer.add(current + 1);
                partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
            }
        }
    }
}