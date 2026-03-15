package model.objects;

import transforms.Col;
import transforms.Point3D;

public class Light {

    private Point3D position;
    private final Col color;

    private double angle;
    private double radius;
    private final double height;

    public Light(Point3D position, Col color) {
        this.position = position;
        this.color = color;

        this.height = position.getZ();
        computeFromPosition();
    }

    public Point3D getPosition() { return position; }

    public void setPosition(Point3D position) {
        this.position = position;
        computeFromPosition();
    }

    public Col getColor() { return color; }

    // spočítá radius a angle z aktuální pozice
    private void computeFromPosition() {
        double x = position.getX();
        double y = position.getY();

        radius = Math.sqrt(x*x + y*y);
        angle = Math.atan2(y, x);
    }

    // zavolá se při spuštění animace
    public void startAnimationFromCurrentPosition() {
        computeFromPosition();
    }

    // jeden krok animace
    public void animate(double speed) {
        angle += speed;

        double x = Math.cos(angle) * radius;
        double y = Math.sin(angle) * radius;

        position = new Point3D(x, y, height);
    }
}