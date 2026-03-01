package model.rasterops;

import model.objectdata.Vertex;
import model.rasterdata.ColorBuffer;
import model.rasterdata.Raster;
import transforms.Col;


public abstract class LineRasterizer {

    Raster raster;
    Col color;

    public LineRasterizer(ColorBuffer raster){
        this.raster = raster;
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {

    }

    protected void drawLine(Vertex v1, Vertex v2) {

    }

    public void setColor(Col color) {
        this.color = color;
    }

    public void setRaster(ColorBuffer raster) {
        this.raster = raster;
    }

    public void rasterize(Vertex v1, Vertex v2) {
        int width = raster.getWidth();
        int height = raster.getHeight();

        // NDC -> screen
        int x1 = (int) ((v1.getPosition().getX() + 1) * 0.5 * width);
        int y1 = (int) ((1 - v1.getPosition().getY()) * 0.5 * height);

        int x2 = (int) ((v2.getPosition().getX() + 1) * 0.5 * width);
        int y2 = (int) ((1 - v2.getPosition().getY()) * 0.5 * height);

        // vezme barvu z prvního vertexu
        setColor(new Col(
                (int) v1.getColor().getR(),
                (int) v1.getColor().getG(),
                (int) v1.getColor().getB()
        ));

        drawLine(x1, y1, x2, y2);
    }
}
