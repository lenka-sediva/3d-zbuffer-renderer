package model.rasterdata;

import transforms.Col;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorBuffer implements Raster<Col> {

    private final BufferedImage img;
    private final Col backgroundColor = new Col(0x16161d);

    public ColorBuffer(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public Col getPixel(int x, int y) {
        return new Col(img.getRGB(x, y));
    }

    @Override
    public void setPixel(int x, int y, Col pixelValue) {
        img.setRGB(x, y, pixelValue.getRGB());
    }


    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(new Color(backgroundColor.getRGB()));
        g.fillRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
    }

    public void present(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public BufferedImage getImg() {
        return img;
    }

}
