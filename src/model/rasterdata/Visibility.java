package model.rasterdata;

import transforms.Col;

public class Visibility {

    private final ColorBuffer cB;
    private final DepthBuffer dB;

    public Visibility(ColorBuffer cB) {
        this.cB = cB;
        this.dB = new DepthBuffer(cB.getWidth(), cB.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        if(cB.isValidAddress(x, y)) {
            // ošetření intervalu hloubky
            if (z < 0.0 || z > 1.0) return;

            double zB = dB.getPixel(x, y);

            if(z < zB) { // interval z (menší = blíž ke kameře)
                cB.setPixel(x, y, color); // obarvení cB
                dB.setPixel(x, y, z); // nastavení nového z pro dB
            }
        }
    }

    // pro testování
     public void setPixel(int x, int y, Col color){
        if (cB.isValidAddress(x, y)){
            cB.setPixel(x, y, color);
        }
     }

     public void clear() {
        cB.clear();
        dB.clear();
     }

    public int getWidth() {
        return cB.getWidth();
    }

    public int getHeight() {
        return cB.getHeight();
    }

    public void resize(int width, int height) {
        cB.resize(width, height);
        dB.resize(width, height);
    }
}
