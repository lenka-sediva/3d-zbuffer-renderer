package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Arrow extends Solid {

    public Arrow() {

        // VERTEX BUFFER

        // --- TĚLO (obdélník jako TRIANGLE_STRIP) ---
        vertexBuffer.add(new Vertex(new Point3D(-0.8,  0.1, 0.5), new Col(0,0,1))); // 0
        vertexBuffer.add(new Vertex(new Point3D(-0.2,  0.1, 0.5), new Col(0,0,1))); // 1
        vertexBuffer.add(new Vertex(new Point3D(-0.8, -0.1, 0.5), new Col(1,0,1))); // 2
        vertexBuffer.add(new Vertex(new Point3D(-0.2, -0.1, 0.5), new Col(1,0,1))); // 3

        // --- HLAVIČKA (1 trojúhelník) ---
        vertexBuffer.add(new Vertex(new Point3D( 0.2,  0.25, 0.5), new Col(0,1,0))); // 4
        vertexBuffer.add(new Vertex(new Point3D( 0.2, -0.25, 0.5), new Col(0,1,0))); // 5
        vertexBuffer.add(new Vertex(new Point3D( 0.6,  0.0,  0.5), new Col(0,1,0))); // 6

        // --- STŘEDNÍ OSA (úsečka) ---
        vertexBuffer.add(new Vertex(new Point3D(-0.2, 0.0, 0.5), new Col(1,1,1))); // 7
        vertexBuffer.add(new Vertex(new Point3D( 0.2, 0.0, 0.5), new Col(1,1,1))); // 8

        // --- HORNÍ OCAS (6 trojúhelníků → 8 vrcholů pro strip) ---
        vertexBuffer.add(new Vertex(new Point3D(-0.6, 0.1, 0.5), new Col(1,0,0))); // 9
        vertexBuffer.add(new Vertex(new Point3D(-0.55,0.2, 0.5), new Col(1,0,0))); //10
        vertexBuffer.add(new Vertex(new Point3D(-0.5, 0.1, 0.5), new Col(1,0.5,0))); //11
        vertexBuffer.add(new Vertex(new Point3D(-0.45,0.2, 0.5), new Col(1,0.5,0))); //12
        vertexBuffer.add(new Vertex(new Point3D(-0.4, 0.1, 0.5), new Col(1,1,0))); //13
        vertexBuffer.add(new Vertex(new Point3D(-0.35,0.2, 0.5), new Col(1,1,0))); //14
        vertexBuffer.add(new Vertex(new Point3D(-0.3, 0.1, 0.5), new Col(1,1,0))); //15
        vertexBuffer.add(new Vertex(new Point3D(-0.25,0.2, 0.5), new Col(1,1,0))); //16

        // --- DOLNÍ OCAS (6 trojúhelníků → 8 vrcholů) ---
        vertexBuffer.add(new Vertex(new Point3D(-0.6, -0.1, 0.5), new Col(0,0,1))); //17
        vertexBuffer.add(new Vertex(new Point3D(-0.55,-0.2, 0.5), new Col(0,0,1))); //18
        vertexBuffer.add(new Vertex(new Point3D(-0.5, -0.1, 0.5), new Col(0,0.5,1))); //19
        vertexBuffer.add(new Vertex(new Point3D(-0.45,-0.2, 0.5), new Col(0,0.5,1))); //20
        vertexBuffer.add(new Vertex(new Point3D(-0.4, -0.1, 0.5), new Col(0,1,1))); //21
        vertexBuffer.add(new Vertex(new Point3D(-0.35,-0.2, 0.5), new Col(0,1,1))); //22
        vertexBuffer.add(new Vertex(new Point3D(-0.3, -0.1, 0.5), new Col(0,1,1))); //23
        vertexBuffer.add(new Vertex(new Point3D(-0.25,-0.2, 0.5), new Col(0,1,1))); //24

        // INDEX BUFFER

        int start;

        // --- TĚLO ---
        start = indexBuffer.size();
        indexBuffer.add(0);
        indexBuffer.add(1);
        indexBuffer.add(2);
        indexBuffer.add(3);
        partBuffer.add(new Part(start, 4, TopologyType.TRIANGLE_STRIP));

        // --- HLAVIČKA ---
        start = indexBuffer.size();
        indexBuffer.add(4);
        indexBuffer.add(5);
        indexBuffer.add(6);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        // --- OSA ---
        start = indexBuffer.size();
        indexBuffer.add(7);
        indexBuffer.add(8);
        partBuffer.add(new Part(start, 2, TopologyType.LINE_LIST));

        // --- HORNÍ OCAS ---
        start = indexBuffer.size();
        for(int i = 9; i <= 16; i++) {
            indexBuffer.add(i);
        }
        partBuffer.add(new Part(start, 8, TopologyType.TRIANGLE_STRIP)); // ORANŽOVÝ

        // --- DOLNÍ OCAS ---
        start = indexBuffer.size();
        for(int i = 17; i <= 24; i++) {
            indexBuffer.add(i);
        }
        partBuffer.add(new Part(start, 8, TopologyType.TRIANGLE_STRIP)); // MODRÝ

        System.out.println("Arrow OK, vertex count: " + vertexBuffer.size());
    }
}