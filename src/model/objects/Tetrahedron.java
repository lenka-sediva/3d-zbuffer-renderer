package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Tetrahedron extends Solid {

    public Tetrahedron() {
        super("čtyřstěn", "T");
        double zOffset = -0.1; // posun

        // VERTEX BUFFER s UV souřadnice pro textuturu
        vertexBuffer.add(new Vertex(new Point3D( 0,  0.5, zOffset), new Col(255,255,0), new Vec2D(0.5, 0.0), new Vec3D(0,0,1), 1.0)); // 0
        vertexBuffer.add(new Vertex(new Point3D(-0.5, -0.5, zOffset), new Col(255,0,0), new Vec2D(0.0, 1.0), new Vec3D(0,0,1), 1.0)); // 1
        vertexBuffer.add(new Vertex(new Point3D( 0.5, -0.5, zOffset), new Col(0,255,0), new Vec2D(1.0, 1.0), new Vec3D(0,0,1), 1.0)); // 2
        vertexBuffer.add(new Vertex(new Point3D( 0,  0, zOffset - 0.7), new Col(255,255,0), new Vec2D(0.5, 0.5), new Vec3D(0,0,-1), 1.0)); // 3 (zadní vrchol)

        // INDEX BUFFER
        int start;

        // přední trojúhelník
        start = indexBuffer.size();
        indexBuffer.add(0);
        indexBuffer.add(1);
        indexBuffer.add(2);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        // levá strana
        start = indexBuffer.size();
        indexBuffer.add(0);
        indexBuffer.add(1);
        indexBuffer.add(3);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        // pravá strana
        start = indexBuffer.size();
        indexBuffer.add(0);
        indexBuffer.add(2);
        indexBuffer.add(3);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        // spodní strana
        start = indexBuffer.size();
        indexBuffer.add(1);
        indexBuffer.add(2);
        indexBuffer.add(3);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
    }
}