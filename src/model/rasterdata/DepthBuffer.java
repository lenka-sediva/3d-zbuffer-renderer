package model.rasterdata;

public class DepthBuffer implements Raster<Double>{

    private final double[][] depthBuffer;
    private final int width;
    private final int height;

    public DepthBuffer(int width, int height) {
        this.depthBuffer = new double[width][height];
        this.width = width;
        this.height = height;
        clear();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Double getPixel(int x, int y) {
        if(isValidAddress(x, y))
            return depthBuffer[x][y];

        return 1.0;
    }

    @Override
    public void setPixel(int x, int y, Double pixelValue) {
        if(isValidAddress(x, y))
            depthBuffer[x][y] = pixelValue;
    }

    @Override
    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                depthBuffer[x][y] = 1.0;
            }
        }
    }
}
