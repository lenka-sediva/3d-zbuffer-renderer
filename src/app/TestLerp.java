package app;

import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import utilities.Lerp;

public class TestLerp {
    public static void main(String[] args) {
        Vertex v1 = new Vertex(new Point3D(1,2,3), new Col(0xFF0000));
        Vertex v2 = new Vertex(new Point3D(2,3,4), new Col(0x00FF00));

        double t = 0.44;

        // LERP = ((1 - t) * v1) + (v2 * t)
        Vertex v3 = v1.mul(1 - t).add(v2.mul(t));
        var v4 = Lerp.compute(v1, v2, t);

        IO.println(v3);
        IO.println(v4);
    }
}