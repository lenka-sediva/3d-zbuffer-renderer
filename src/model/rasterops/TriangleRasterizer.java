package model.rasterops;

import model.objectdata.Vertex;
import model.rasterdata.Visibility;
import transforms.Col;

import java.util.function.Function;

public class TriangleRasterizer {

    private final Visibility img;
    private Function<Vertex, Col> shader;

    public TriangleRasterizer(Visibility img, Function<Vertex, Col> shader) {
        this.img = img;
        this.shader = shader;
    }

    public void setShader(Function<Vertex, Col> shader) {
        this.shader = shader;
    }

    public void rasterize(Vertex vA, Vertex vB, Vertex vC) {
        double ax = vA.getX(), ay = vA.getY(), az = vA.getZ();
        double bx = vB.getX(), by = vB.getY(), bz = vB.getZ();
        double cx = vC.getX(), cy = vC.getY(), cz = vC.getZ();

        Col colA = vA.getColor();
        Col colB = vB.getColor();
        Col colC = vC.getColor();

        // seřazení podle Y
        if (ay > by) {
            double tx=ax; ax=bx; bx=tx;
            double ty=ay; ay=by; by=ty;
            double tz=az; az=bz; bz=tz;
            Col tc=colA; colA=colB; colB=tc;
        }

        if (by > cy) {
            double tx=bx; bx=cx; cx=tx;
            double ty=by; by=cy; cy=ty;
            double tz=bz; bz=cz; cz=tz;
            Col tc=colB; colB=colC; colC=tc;
        }

        if (ay > by) {
            double tx=ax; ax=bx; bx=tx;
            double ty=ay; ay=by; by=ty;
            double tz=az; az=bz; bz=tz;
            Col tc=colA; colA=colB; colB=tc;
        }

        int yStart = (int)Math.ceil(ay);
        int yMiddle = (int)Math.ceil(by);
        int yEnd = (int)Math.ceil(cy);

        yStart = Math.max(yStart, 0);
        yEnd = Math.min(yEnd, img.getHeight()-1);

        // HORNÍ část trojúhelníku
        for (int y = yStart; y < yMiddle; y++) {

            if (y < 0 || y >= img.getHeight()) continue;

            double tAB = (by - ay) != 0 ? (y - ay) / (by - ay) : 0;
            double tAC = (cy - ay) != 0 ? (y - ay) / (cy - ay) : 0;

            double xAB = ax + tAB * (bx - ax);
            double zAB = az + tAB * (bz - az);
            Col colAB = colA.mul(1 - tAB).add(colB.mul(tAB));

            double xAC = ax + tAC * (cx - ax);
            double zAC = az + tAC * (cz - az);
            Col colAC = colA.mul(1 - tAC).add(colC.mul(tAC));

            if (xAB > xAC) {
                double tx=xAB; xAB=xAC; xAC=tx;
                double tz=zAB; zAB=zAC; zAC=tz;
                Col tc=colAB; colAB=colAC; colAC=tc;
            }

            int xStart = Math.max((int)Math.ceil(xAB), 0);
            int xEnd = Math.min((int)Math.floor(xAC), img.getWidth()-1);

            for (int x = xStart; x <= xEnd; x++) {

                double t = (xAC - xAB) != 0 ? (x - xAB) / (xAC - xAB) : 0;

                double z = zAB + t * (zAC - zAB);
                Col col = colAB.mul(1 - t).add(colAC.mul(t));

                img.setPixelWithZTest(x, y, z, col);
            }
        }

        // DOLNÍ část trojúhelníku
        for (int y = yMiddle; y <= yEnd; y++) {

            if (y < 0 || y >= img.getHeight()) continue;

            double tBC = (cy - by) != 0 ? (y - by) / (cy - by) : 0;
            double tAC = (cy - ay) != 0 ? (y - ay) / (cy - ay) : 0;

            double xBC = bx + tBC * (cx - bx);
            double zBC = bz + tBC * (cz - bz);
            Col colBC = colB.mul(1 - tBC).add(colC.mul(tBC));

            double xAC = ax + tAC * (cx - ax);
            double zAC = az + tAC * (cz - az);
            Col colAC = colA.mul(1 - tAC).add(colC.mul(tAC));

            if (xBC > xAC) {
                double tx=xBC; xBC=xAC; xAC=tx;
                double tz=zBC; zBC=zAC; zAC=tz;
                Col tc=colBC; colBC=colAC; colAC=tc;
            }

            int xStart = Math.max((int)Math.ceil(xBC), 0);
            int xEnd = Math.min((int)Math.floor(xAC), img.getWidth()-1);

            for (int x = xStart; x <= xEnd; x++) {

                double t = (xAC - xBC) != 0 ? (x - xBC) / (xAC - xBC) : 0;

                double z = zBC + t * (zAC - zBC);
                Col col = colBC.mul(1 - t).add(colAC.mul(t));

                img.setPixelWithZTest(x, y, z, col);
            }
        }
    }
}