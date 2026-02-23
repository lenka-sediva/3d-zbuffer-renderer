package view;

import model.rasterdata.ColorBuffer;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
    private final ColorBuffer raster;

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new ColorBuffer(width, height);

        clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.present(g);
    }

    public ColorBuffer getRaster() {
        return raster;
    }

    public void clear() {
        raster.clear();
    }
}
