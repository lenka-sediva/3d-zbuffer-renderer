package model.objectdata;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Vertex implements Vectorizable<Vertex>{
    private final Point3D position;
    private final Col col;
    private Vec2D texUV;
    private Vec3D normal;
    private double one;

    public Vertex(Point3D position, Col col) {
        this(position, col, new Vec2D(0,0), new Vec3D(0,0,0), 1.0);
    }

    public Vertex(Point3D position, Col col, Vec2D texUV, Vec3D normal, double one) {
        this.position = position;
        this.col = col;
        this.texUV = texUV;
        this.normal = normal;
        this.one = one;
    }

    public Vertex(Point3D position) {
        this(position, new Col(1., 1., 1.));
    }

    public Point3D getPosition() {
        return position;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getZ() {
        return position.getZ();
    }

    public Col getColor() {
        return col;
    }

    @Override
    public Vertex mul(double scalar) {
        return new Vertex(
                position.mul(scalar),
                col.mul(scalar),
                texUV.mul(scalar),
                normal.mul(scalar),
                one * scalar
        );
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(
                position.add(v.position),
                col.add(v.col),
                texUV.add(v.texUV),
                normal.add(v.normal),
                one + v.one
        );
    }

    @Override
    public String toString() {
        return String.format("Vertex { position = %s, color = %s, texUV = %s, normal = %s, one = %.2f }",
                position, col, texUV, normal, one);
    }
}
