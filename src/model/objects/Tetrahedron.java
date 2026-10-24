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
        super("tetrahedron", "T");
        double zOffset = 0.2;

        Point3D p0 = new Point3D( 0, 0.5, zOffset);
        Point3D p1 = new Point3D(-0.5, -0.5, zOffset);
        Point3D p2 = new Point3D( 0.5, -0.5, zOffset);
        Point3D p3 = new Point3D( 0, 0, zOffset - 0.7);

        // normály vypočítané jako cross product hran každé stěny
        Vec3D nFront = faceNormal(p0, p1, p2);
        Vec3D nLeft = faceNormal(p0, p1, p3);
        Vec3D nRight = faceNormal(p0, p3, p2);
        Vec3D nBottom = faceNormal(p1, p3, p2);

        // VERTEX BUFFER
        vertexBuffer.add(new Vertex(p0, new Col(255,255,0), new Vec2D(0.5, 0.0), nFront, 1.0));
        vertexBuffer.add(new Vertex(p1, new Col(255,0,0), new Vec2D(0.0, 1.0), nFront, 1.0));
        vertexBuffer.add(new Vertex(p2, new Col(0,255,0), new Vec2D(1.0, 1.0), nFront, 1.0));

        vertexBuffer.add(new Vertex(p0, new Col(255,255,0), new Vec2D(0.5, 0.0), nLeft, 1.0));
        vertexBuffer.add(new Vertex(p1, new Col(255,0,0), new Vec2D(0.0, 1.0), nLeft, 1.0));
        vertexBuffer.add(new Vertex(p3, new Col(0,0,255), new Vec2D(0.5, 0.5), nLeft, 1.0));

        vertexBuffer.add(new Vertex(p0, new Col(255,255,0), new Vec2D(0.5, 0.0), nRight, 1.0));
        vertexBuffer.add(new Vertex(p3, new Col(0,0,255), new Vec2D(0.5, 0.5), nRight, 1.0));
        vertexBuffer.add(new Vertex(p2, new Col(0,255,0), new Vec2D(1.0, 1.0), nRight, 1.0));

        vertexBuffer.add(new Vertex(p1, new Col(255,0,0), new Vec2D(0.0, 1.0), nBottom, 1.0));
        vertexBuffer.add(new Vertex(p3, new Col(0,0,255), new Vec2D(0.5, 0.5), nBottom, 1.0));
        vertexBuffer.add(new Vertex(p2, new Col(0,255,0), new Vec2D(1.0, 1.0), nBottom, 1.0));

        // INDEX BUFFER = každá stěna má vlastní vertexy
        for (int i = 0; i < 4; i++) {
            int base = i * 3;
            int s = indexBuffer.size();
            indexBuffer.add(base); indexBuffer.add(base+1); indexBuffer.add(base+2);
            partBuffer.add(new Part(s, 3, TopologyType.TRIANGLE_LIST));
        }
    }

    // vypočítá normálu stěny ze skalárního součinu hran
    private Vec3D faceNormal(Point3D a, Point3D b, Point3D c) {
        Vec3D ab = new Vec3D(b.getX()-a.getX(), b.getY()-a.getY(), b.getZ()-a.getZ());
        Vec3D ac = new Vec3D(c.getX()-a.getX(), c.getY()-a.getY(), c.getZ()-a.getZ());
        return ab.cross(ac).normalized().orElse(new Vec3D(0,0,1));
    }
}