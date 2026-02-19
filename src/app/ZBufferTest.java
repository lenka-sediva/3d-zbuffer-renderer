package app;

import model.rasterdata.Visibility;
import transforms.Col;

public class ZBufferTest {

    public static void runTest(Visibility vB) {
        // drawSquare 9x
        // RGB
        drawSquare(vB, 10, 10, 100, 100, new Col(1.0, 0, 0), 0.8);
        drawSquare(vB, 30, 30, 100, 100, new Col(0, 1.0, 0), 0.5);
        drawSquare(vB, 50, 50, 100, 100, new Col(0, 0, 1.0), 0.3);

        // RGB 2
        drawSquare(vB, 200, 10, 100, 100, new Col(1.0, 0, 0), 0.3);
        drawSquare(vB, 220, 30, 100, 100, new Col(0, 1.0, 0), 0.5);
        drawSquare(vB, 240, 50, 100, 100, new Col(0, 0, 1.0), 0.8);

        // CMY
        drawSquare(vB, 400, 10, 100, 100, new Col(1.0, 1.0, 0), 0.5); // yellow
        drawSquare(vB, 420, 30, 100, 100, new Col(0, 1.0, 1.0), 0.8); // cyan
        drawSquare(vB, 440, 50, 100, 100, new Col(1.0, 0, 1.0), 0.3); // magenta
    }

    private static void drawSquare(Visibility vB, int xStart, int yStart, int squareWidth, int squareHeight, Col color, double depth) {
        // nested loop
        for (int y = yStart; y < yStart + squareHeight; y++) {
            for (int x = xStart; x < xStart + squareWidth; x++) {
                vB.setPixelWithZTest(x, y, depth, color);
            }
        }
    }
}
