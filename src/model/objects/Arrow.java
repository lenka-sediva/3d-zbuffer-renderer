package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Arrow extends Solid {

    public Arrow() {
        super("šipka", "I");

        double z = -0.5;

        // VERTEX BUFFER
        // střed (sdílený vrchol)
        vertexBuffer.add(new Vertex(new Point3D(-0.1,  0., z), new Col(255,255,255))); // 0

        // horní ocas (6 trojúhelníků = 8 vertexů, strip)
        vertexBuffer.add(new Vertex(new Point3D(-0.7,  0., z), new Col(0,0,255))); // 1
        vertexBuffer.add(new Vertex(new Point3D(-0.8, 0.2, z), new Col(0,0,255))); // 2
        vertexBuffer.add(new Vertex(new Point3D(-0.6,  0., z), new Col(0,0,255))); // 3
        vertexBuffer.add(new Vertex(new Point3D(-0.5, 0.2, z), new Col(0,0,255))); // 4
        vertexBuffer.add(new Vertex(new Point3D(-0.4,  0., z), new Col(0,0,255))); // 5
        vertexBuffer.add(new Vertex(new Point3D(-0.3, 0.2, z), new Col(0,0,255))); // 6
        vertexBuffer.add(new Vertex(new Point3D(-0.2,  0., z), new Col(0,150,255))); // 7

        // dolní ocas
        vertexBuffer.add(new Vertex(new Point3D(-0.7, -0., z), new Col(255,0,255))); // 8
        vertexBuffer.add(new Vertex(new Point3D(-0.8,-0.2, z), new Col(255,0,255))); // 9
        vertexBuffer.add(new Vertex(new Point3D(-0.6, -0., z), new Col(255,0,255))); // 10
        vertexBuffer.add(new Vertex(new Point3D(-0.5,-0.2, z), new Col(255,0,255))); // 11
        vertexBuffer.add(new Vertex(new Point3D(-0.4, -0., z), new Col(255,0,255))); // 12
        vertexBuffer.add(new Vertex(new Point3D(-0.3,-0.2, z), new Col(255,0,255))); // 13
        vertexBuffer.add(new Vertex(new Point3D(-0.2, -0., z), new Col(255,150,255))); // 14

        // hlavička
        vertexBuffer.add(new Vertex(new Point3D(0.3,  0.2, z), new Col(0,255,0))); // 15
        vertexBuffer.add(new Vertex(new Point3D(0.3, -0.2, z), new Col(0,255,0))); // 16
        vertexBuffer.add(new Vertex(new Point3D(0.5,  0.0,  z), new Col(0,255,0))); // 17
        vertexBuffer.add(new Vertex(new Point3D(0.3,  0., z), new Col(255,255,255))); // 18 = pomocný bod uprostřed pro spojení se šipkou

        // INDEX BUFFER
        int start;

        // horní ocas
        start = indexBuffer.size();
        indexBuffer.add(1);
        indexBuffer.add(2);
        indexBuffer.add(3);
        indexBuffer.add(4);
        indexBuffer.add(5);
        indexBuffer.add(6);
        indexBuffer.add(7);
        indexBuffer.add(0); // napojení na bílý střed
        partBuffer.add(new Part(start, 8, TopologyType.TRIANGLE_STRIP));

        // dolní ocas
        start = indexBuffer.size();
        indexBuffer.add(8);
        indexBuffer.add(9);
        indexBuffer.add(10);
        indexBuffer.add(11);
        indexBuffer.add(12);
        indexBuffer.add(13);
        indexBuffer.add(14);
        indexBuffer.add(0); // napojení na bílý střed
        partBuffer.add(new Part(start, 8, TopologyType.TRIANGLE_STRIP));

        // hlavička
        start = indexBuffer.size();
        indexBuffer.add(15);
        indexBuffer.add(16);
        indexBuffer.add(17);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        // tělo (úsečka)
        start = indexBuffer.size();
        indexBuffer.add(0);
        indexBuffer.add(18);
        partBuffer.add(new Part(start, 2, TopologyType.LINE_LIST));
    }
}