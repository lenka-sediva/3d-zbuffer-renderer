package app;

import model.objectdata.Lerp;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class TestLerp {
    public static void main(String[] args) {
        Vertex v1 = new Vertex(new Point3D(1,2,3), new Col(0xFF0000),
                new Vec2D(0,0), new Vec3D(0,0,1), 1.0);
        Vertex v2 = new Vertex(new Point3D(4,5,6), new Col(0x00FF00),
                new Vec2D(1,1), new Vec3D(1,0,0), 1.0);
        double t = 0.44;

        Lerp<Vertex> lerp = new Lerp<>();
        Vertex result = lerp.lerp(v1, v2, t);

        System.out.println("v1 = " + v1);
        System.out.println("v2 = " + v2);
        System.out.println("result = " + result);
    }
}