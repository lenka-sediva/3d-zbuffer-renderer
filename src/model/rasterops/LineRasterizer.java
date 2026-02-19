package model.rasterops;

import model.objectdata.Line;
import model.rasterdata.ColorBuffer;
import java.awt.*;

public abstract class LineRasterizer {

    ColorBuffer raster;
    Color color;

    public LineRasterizer(ColorBuffer raster){
        this.raster = raster;
    }

    public void rasterize(Line line) {
        setColor(new Color(line.getColor()));
        drawLine((int) Math.round(line.getStart().getX()), (int) Math.round(line.getStart().getY()), (int) Math.round(line.getEnd().getX()), (int) Math.round(line.getEnd().getY()));
    }

    public void rasterize(int x1, int y1, int x2, int y2, Color color) {
        setColor(color);
        drawLine(x1, y1, x2, y2);
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {

    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void setRaster(ColorBuffer raster) {
        this.raster = raster;
    }
}
