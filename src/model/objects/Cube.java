package model.objects;

import model.objectdata.Part;
import model.objectdata.Solid;
import model.objectdata.TopologyType;
import model.objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Cube extends Solid {
    /**
     * @param center střed krychle
     * @param size   délka hrany
     */
    public Cube(Point3D center, double size) {
        super("cube", "K");

        double h = size / 2.0;
        double cx = center.getX();
        double cy = center.getY();
        double cz = center.getZ();

        // odstíny modré pro každou stěnu
        Col colSide = new Col(0.2, 0.4, 1.0); // světle modrá = boční stěny
        Col colTop = new Col(0.4, 0.6, 1.0); // světlější modrá = horní
        Col colBottom = new Col(0.0, 0.1, 0.6); // tmavá modrá = spodní

        // UV oblasti v atlasu (256x256, čtvrtiny 128x128)
        // boční stěna (hlína + tráva nahoře) = vlevo nahoře
        Vec2D sideUV00 = new Vec2D(0.0, 1.0); // nahoře vlevo => tráva
        Vec2D sideUV10 = new Vec2D(0.5, 1.0); // nahoře vpravo => tráva
        Vec2D sideUV01 = new Vec2D(0.0, 0.5); // dole vlevo => hlína
        Vec2D sideUV11 = new Vec2D(0.5, 0.5); // dole vpravo => hlína

        // tráva = vpravo nahoře
        Vec2D grassUV00 = new Vec2D(0.5, 1.0);
        Vec2D grassUV10 = new Vec2D(1.0, 1.0);
        Vec2D grassUV01 = new Vec2D(0.5, 0.5);
        Vec2D grassUV11 = new Vec2D(1.0, 0.5);

        // hlína = vlevo dole
        Vec2D dirtUV00 = new Vec2D(0.0, 0.5);
        Vec2D dirtUV10 = new Vec2D(0.5, 0.5);
        Vec2D dirtUV01 = new Vec2D(0.0, 0.0);
        Vec2D dirtUV11 = new Vec2D(0.5, 0.0);

        // každá stěna má vlastní 4 vertexy (kvůli správným normálám a UV)
        // UV jsou přiřazeny tak, že tráva je NAHOŘE (cy+h) a hlína DOLE (cy-h)

        // PŘEDNÍ stěna (normála +Z) = boční
        Vec3D nFront = new Vec3D(0, 0, 1);
        int base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy-h, cz+h), colSide, sideUV01, nFront, 1.0)); // dole vlevo  → UV dole vlevo
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy-h, cz+h), colSide, sideUV11, nFront, 1.0)); // dole vpravo → UV dole vpravo
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy+h, cz+h), colSide, sideUV10, nFront, 1.0)); // nahoře vpravo → UV nahoře vpravo
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy+h, cz+h), colSide, sideUV00, nFront, 1.0)); // nahoře vlevo  → UV nahoře vlevo
        addFace(base);

        // ZADNÍ stěna (normála -Z) = boční
        Vec3D nBack = new Vec3D(0, 0, -1);
        base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy-h, cz-h), colSide, sideUV11, nBack, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy-h, cz-h), colSide, sideUV01, nBack, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy+h, cz-h), colSide, sideUV00, nBack, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy+h, cz-h), colSide, sideUV10, nBack, 1.0));
        addFace(base);

        // LEVÁ stěna (normála -X) = boční
        Vec3D nLeft = new Vec3D(-1, 0, 0);
        base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy-h, cz-h), colSide, sideUV01, nLeft, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy-h, cz+h), colSide, sideUV11, nLeft, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy+h, cz+h), colSide, sideUV10, nLeft, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy+h, cz-h), colSide, sideUV00, nLeft, 1.0));
        addFace(base);

        // PRAVÁ stěna (normála +X) = boční
        Vec3D nRight = new Vec3D(1, 0, 0);
        base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy-h, cz+h), colSide, sideUV01, nRight, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy-h, cz-h), colSide, sideUV11, nRight, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy+h, cz-h), colSide, sideUV10, nRight, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy+h, cz+h), colSide, sideUV00, nRight, 1.0));
        addFace(base);

        // HORNÍ stěna (normála +Y) = čistá tráva
        Vec3D nTop = new Vec3D(0, 1, 0);
        base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy+h, cz+h), colTop, grassUV00, nTop, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy+h, cz+h), colTop, grassUV10, nTop, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy+h, cz-h), colTop, grassUV11, nTop, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy+h, cz-h), colTop, grassUV01, nTop, 1.0));
        addFace(base);

        // SPODNÍ stěna (normála -Y) = čistá hlína
        Vec3D nBottom = new Vec3D(0, -1, 0);
        base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy-h, cz-h), colBottom, dirtUV00, nBottom, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy-h, cz-h), colBottom, dirtUV10, nBottom, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx+h, cy-h, cz+h), colBottom, dirtUV11, nBottom, 1.0));
        vertexBuffer.add(new Vertex(new Point3D(cx-h, cy-h, cz+h), colBottom, dirtUV01, nBottom, 1.0));
        addFace(base);
    }

    /**
     * Přidá stěnu jako 2 trojúhelníky (quad) od indexu base
     * Vertexy stěny jsou vždy 4 po sobě jdoucí: base, base+1, base+2, base+3
     */
    private void addFace(int base) {
        // první trojúhelník
        int start = indexBuffer.size();
        indexBuffer.add(base);
        indexBuffer.add(base + 1);
        indexBuffer.add(base + 2);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));

        // druhý trojúhelník
        start = indexBuffer.size();
        indexBuffer.add(base);
        indexBuffer.add(base + 2);
        indexBuffer.add(base + 3);
        partBuffer.add(new Part(start, 3, TopologyType.TRIANGLE_LIST));
    }

    public Cube() {
        this(new Point3D(2, 0, 0), 1.0);
    }
}