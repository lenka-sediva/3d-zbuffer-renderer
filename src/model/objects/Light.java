package model.objects;

import transforms.Col;
import transforms.Point3D;

public class Light {
    private Point3D position;
    private final Col color; // barva difúzní složky

    public Light(Point3D position, Col color) {
        this.position = position;
        this.color = color;
    }

    public Point3D getPosition() { return position; }
    public void setPosition(Point3D position) { this.position = position; }
    public Col getColor() { return color; }
}