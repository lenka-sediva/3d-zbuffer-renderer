package model.objectdata;

import transforms.*;
import utilities.Vectorizable;

import java.util.Optional;

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col col;
    private final Vec2D texUV;
    private final Vec3D normal;
    private final double one;
    private final Point3D worldPosition; // světové souřadnice pro osvětlení

    public Col getColor() {
        return col;
    }

    public Vec2D getTexUV() {
        return texUV;
    }

    public Vec3D getNormal() {
        return normal;
    }

    public double getOne() {
        return one;
    }

    public Vertex(Point3D position, Col col) {
        this(position, col, new Vec2D(0,0), new Vec3D(0,0,0), 1.0, position);
    }

    public Vertex(Point3D position, Col col, Vec2D texUV, Vec3D normal, double one, Point3D worldPosition) {
        this.position = position;
        this.col = col;
        this.texUV = texUV;
        this.normal = normal;
        this.one = one;
        this.worldPosition = worldPosition;
    }

    public Vertex(Point3D position, Col col, Vec2D texUV, Vec3D normal, double one) {
        this.position = position;
        this.col = col;
        this.texUV = texUV;
        this.normal = normal;
        this.one = one;
        this.worldPosition = position;
    }

    public Vertex(Point3D position) {
        this(position, new Col(1., 1., 1.));
    }

    public Point3D getPosition() {
        return position;
    }

    public Point3D getWorldPos() {
        return worldPosition;
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

    @Override
    public Vertex mul(double scalar) {
        return new Vertex(
                this.position.mul(scalar),
                this.col.mul(scalar).saturate(),
                this.texUV.mul(scalar),
                this.normal.mul(scalar),
                this.one * scalar,
                this.worldPosition
        );
    }

    @Override
    public Vertex add(Vertex other) {
        return new Vertex(
                this.position.add(other.getPosition()),
                this.col.add(other.getColor()).saturate(),
                this.texUV.add(other.getTexUV()),
                this.normal.add(other.getNormal()),
                this.one + other.getOne(),
                new Point3D(
                        this.worldPosition.getX() + other.getWorldPos().getX(),
                        this.worldPosition.getY() + other.getWorldPos().getY(),
                        this.worldPosition.getZ() + other.getWorldPos().getZ()
                )
        );
    }

    // vrací nový dehomogenizovaný Vertex (dehomogenizuje všechy atributy)
    public Optional<Vertex> dehomog() {
        double w = position.getW();

        // ošetření dělení 0
        if (w == 0) {
            return Optional.empty();
        }

        return Optional.of(new Vertex(
                this.position.mul(1 / w),
                this.col.mul(1 / w).saturate(),
                this.texUV.mul(1 / w),
                this.normal.mul(1 / w),
                this.one / w,
                this.worldPosition // nedehomogenizuje se
        ));
    }

    // transformace vertexu maticí (transformuje pozici, normálu a světové souřadnice)
    public Vertex transf(Mat4 m) {
        Vec3D transformedNormal = new Vec3D(
                new Point3D(normal.getX(), normal.getY(), normal.getZ(), 0).mul(m)
        );
        return new Vertex(position.mul(m), col, texUV, transformedNormal, one, worldPosition);
    }

    public Vec2D getTextureCoordinates() {
        return texUV.mul(1/one);
    }

    @Override
    public String toString() {
        return "Vertex { " +
                "position = " + position +
                ", col = " + col +
                ", texUV = " + texUV +
                ", normal = " + normal +
                ", one = " + one +
                '}';
    }
}
