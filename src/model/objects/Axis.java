package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Axis extends Solid {

    public Axis() {
        super("Osy XYZ", "-");

        double len  = 1.0; // délka osy
        double head = 0.15; // délka hlavičky šipky
        double wide = 0.05; // šířka hlavičky šipky

        Col red = new Col(1.0, 0.0, 0.0); // X
        Col green = new Col(0.0, 1.0, 0.0); // Y
        Col blue = new Col(0.0, 0.0, 1.0); // Z

        Vec3D n = new Vec3D(0, 0, 1);
        Vec2D uv = new Vec2D(0, 0);

        // X OSA (červená)
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), red, uv, n, 1.0)); // 0 - střed X
        vertexBuffer.add(new Vertex(new Point3D(len - head, 0, 0), red, uv, n, 1.0)); // 1 - konec úsečky
        vertexBuffer.add(new Vertex(new Point3D(len, 0, 0), red, uv, n, 1.0)); // 2 - špička
        vertexBuffer.add(new Vertex(new Point3D(len - head, wide, 0), red, uv, n, 1.0)); // 3 - hlavička nahoře
        vertexBuffer.add(new Vertex(new Point3D(len - head, -wide, 0), red, uv, n, 1.0)); // 4 - hlavička dole

        // Y OSA (zelená)
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), green, uv, n, 1.0)); // 5 - střed Y
        vertexBuffer.add(new Vertex(new Point3D(0, len - head, 0), green, uv, n, 1.0)); // 6 - konec úsečky
        vertexBuffer.add(new Vertex(new Point3D(0, len, 0), green, uv, n, 1.0)); // 7 - špička
        vertexBuffer.add(new Vertex(new Point3D(wide, len - head, 0), green, uv, n, 1.0)); // 8 - hlavička vpravo
        vertexBuffer.add(new Vertex(new Point3D(-wide, len - head, 0), green, uv, n, 1.0)); // 9 - hlavička vlevo

        // Z OSA (modrá)
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), blue, uv, n, 1.0)); // 10 - střed Z
        vertexBuffer.add(new Vertex(new Point3D(0, 0, len - head), blue, uv, n, 1.0)); // 11 - konec úsečky
        vertexBuffer.add(new Vertex(new Point3D(0, 0, len), blue, uv, n, 1.0)); // 12 - špička
        vertexBuffer.add(new Vertex(new Point3D(wide, 0, len - head), blue, uv, n, 1.0)); // 13 - hlavička vpravo
        vertexBuffer.add(new Vertex(new Point3D(-wide, 0, len - head), blue, uv, n, 1.0)); // 14 - hlavička vlevo

        int start;

        // ÚSEČKY (střed → základna hlavičky)
        start = indexBuffer.size();
        indexBuffer.add(0); indexBuffer.add(1); // X
        partBuffer.add(new Part(start, 2, TopologyType.LINE_LIST));

        start = indexBuffer.size();
        indexBuffer.add(5); indexBuffer.add(6); // Y
        partBuffer.add(new Part(start, 2, TopologyType.LINE_LIST));

        start = indexBuffer.size();
        indexBuffer.add(10); indexBuffer.add(11); // Z
        partBuffer.add(new Part(start, 2, TopologyType.LINE_LIST));

        // HLAVIČKY ŠIPEK (špička + 2 rohy základny)
        start = indexBuffer.size();
        indexBuffer.add(2); indexBuffer.add(3); indexBuffer.add(4); // X
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        start = indexBuffer.size();
        indexBuffer.add(7); indexBuffer.add(8); indexBuffer.add(9); // Y
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        start = indexBuffer.size();
        indexBuffer.add(12); indexBuffer.add(13); indexBuffer.add(14); // Z
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
    }
}