package model.objects;

import transforms.Col;
import transforms.Point3D;

public class Light {
    private Point3D position;
    private final Col color; // barva difúzní složky
    private final double ambient; // intenzita ambientní složky (0.0 - 1.0)

    public Light(Point3D position, Col color, double ambient) {
        this.position = position;
        this.color = color;
        this.ambient = ambient;
    }

    public Point3D getPosition() { return position; }
    public void setPosition(Point3D position) { this.position = position; }
    public Col getColor() { return color; }
    public double getAmbient() { return ambient; }
}