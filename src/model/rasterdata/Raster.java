package model.rasterdata;

public interface Raster<T> {

    /**
     * Returns the number of columns in this Raster
     *
     * @return number of column
     */
    int getWidth();

    /**
     * Returns the number of rows in this Raster
     *
     * @return number of rows
     */
    int getHeight();

    /**
     * Retrieves color stored at the provided pixel address
     *
     * @param x column index
     * @param y row index
     * @return The color or depth if pixel address is valid
     */
    T getPixel(int x, int y);

    /**
     * Updates the pixel at the provided pixel address
     *
     * @param x column index
     * @param y row index
     * @param pixelValue a color or depth to write
     */
    void setPixel(int x, int y, T pixelValue);

    void clear();

    default boolean isValidAddress(int x, int y) {
        // checks validity of the given address
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
}
