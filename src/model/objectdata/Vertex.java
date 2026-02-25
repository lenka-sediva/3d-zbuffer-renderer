package model.objectdata;

import transforms.Col;
import transforms.Point3D;

public class Vertex {
    private final Point3D position;
    private final Col col;
    // TODO UV, normal, one

    public Vertex(Point3D position, Col col) {
        this.position = position;
        this.col = col;
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

    public Col getCol() {
        return col;
    }
}
