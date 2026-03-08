package model.rasterops;

import model.objectdata.Vertex;
import model.rasterdata.Visibility;
import model.rasterdata.Raster;
import transforms.Col;


public abstract class LineRasterizer {

    Visibility raster;
    Col color;

    public LineRasterizer(Visibility raster){
        this.raster = raster;
    }

    protected void drawLine(int x1, int y1, double z1, int x2, int y2, double z2) {

    }

    protected void drawLine(Vertex v1, Vertex v2) {

    }

    public void setColor(Col color) {
        this.color = color;
    }

    public void setRaster(Visibility raster) {
        this.raster = raster;
    }

    public void rasterize(Vertex v1, Vertex v2) {
        int width = raster.getWidth();
        int height = raster.getHeight();

        // už je v screen coordinates
        int x1 = (int) v1.getX();
        int y1 = (int) v1.getY();
        double z1 = v1.getZ();

        int x2 = (int) v2.getX();
        int y2 = (int) v2.getY();
        double z2 = v2.getZ();

        // vezme barvu z prvního vertexu
        setColor(new Col(
                (int) (v1.getColor().getR() * 255),
                (int) (v1.getColor().getG() * 255),
                (int) (v1.getColor().getB() * 255)
        ));

        drawLine(x1, y1, z1, x2, y2, z2);
    }
}
