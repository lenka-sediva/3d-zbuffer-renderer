package model.rasterops;

import model.rasterdata.ColorBuffer;
import model.rasterdata.Visibility;

/**
 * Bresenhamův algoritmus
 * S podporou tlustých čar (efekt kapsle)
 *
 * Popis:
 * - postupně prochází osu x nebo y od počátečního bodu k cílovému
 * - pro každý krok rozhoduje, zda se má posunout o 1 pixel nahoru/dolů nebo zůstat na stejné řadě
 * - to dělá na základě chybové hodnoty, která určuje, jak daleko je aktutální bod od ideální úsečky
 * - celý výpočet probíhá pouze pomocí celých čísel
 *
 * Výhody:
 * - je rychlý - využívá jen celočíselné sčítání a odčítání
 * - jednoduchá implementace - funguje na principu krokového rozhodování
 *
 * Nevýhody:
 * - neřeší anti-aliasing - úsečky jsou ostré bez vyhlazení hran
 * - obtížnější rozšíření pro barevné gradienty, jelikož se barva musí interpolovat zvlášť
 * - pro plnění oblastí by bylo potřeba kombinovat s jinými algoritmy
 */

public class LineRasterizerBresenham extends LineRasterizer {

    public LineRasterizerBresenham(Visibility raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, double z1, int x2, int y2, double z2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1; // směr po x (napravo/doleva)
        int sy = y1 < y2 ? 1 : -1; // směr po y (dolů/nahoru)

        int error = dx - dy; // počáteční chyba (odchylka) od ideální úsečky

        double z = z1;
        double dz = (dx > dy) ? (z2 - z1) / dx : (z2 - z1) / dy;
        if (dx == 0 && dy == 0) dz = 0;

        while (true) {
            // vykreslí pixel s Z testem
            raster.setPixelWithZTest(x1, y1, z, color);

            if (x1 == x2 && y1 == y2) break; // konec při dosažení koncového bodu

            int e2 = 2 * error;

            // posun po x
            if (e2 > -dy) {
                error -= dy;
                x1 += sx;
                z += dz;
            }

            // posun po y
            if (e2 < dx) {
                error += dx;
                y1 += sy;
                z += dz;
            }
        }
    }
}
