package view;

import model.objectdata.Solid;
import model.rasterdata.ColorBuffer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Canvas extends JPanel {
    private final ColorBuffer raster;
    private List<Solid> solids;
    private String currentObjectName = "šipka";
    private String currentProjectionText = "Perspektivní projekce [P]";

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new ColorBuffer(width, height);

        clear();
    }

    public void setCurrentProjectionText(String text) {
        this.currentProjectionText = text;
    }

    public void setCurrentObject(String name) {
        this.currentObjectName = name;
        repaint();
    }

    public void setObjects(java.util.List<Solid> solids) {
        this.solids = solids;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.present(g);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));

        // levý dolní roh = nápověda
        g.setColor(Color.ORANGE);
        g.drawString("Nápověda:", 10, getHeight() - 135);
        g.setColor(Color.WHITE);
        g.drawString("[šipky] posun objektu", 10, getHeight() - 110);
        g.drawString("[R] animace rotace objektu v čase", 10, getHeight() - 85);
        g.drawString("[+] zvětšení objektu, [-] zmenšení objektu", 10, getHeight() - 60);
        g.drawString("[C] smazání, [O] původní stav, [XYZ] rotace podle os", 10, getHeight() - 35);
        g.drawString("Aktuálně vybraný objekt: ", 10, getHeight() - 10);
        g.setColor(Color.ORANGE);
        g.drawString(currentObjectName, 205, getHeight() - 10);

        // levý horní roh = seznam objektů
        int y = 25;
        for (Solid s : solids) {
            if (s.isSelected()) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString("[" + s.getShortcut() + "] " + s.getName(), 10, y);
            y += 25;
        }

        // pravý dolní roh = projekce
        g.setColor(Color.WHITE);
        g.drawString(currentProjectionText, getWidth() - 200, getHeight() - 10);

        // pravý horní roh = pozorovatel
        g.setColor(Color.ORANGE);
        g.drawString("Pozorovatel:", getWidth() - 105, 25);
        g.setColor(Color.WHITE);
        g.drawString("[WASD] pohyb", getWidth() - 120, 50);
        g.drawString("[Q] vlevo, [E] vpravo", getWidth() - 160, 75);
        g.drawString("[SHIFT] nahoru, [CTRL] dolů", getWidth() - 225, 100);
        //g.drawString(currentCameraMode + " [MEZERNÍK]", getWidth() - 170, 125);
    }

    public ColorBuffer getRaster() {
        return raster;
    }

    public void clear() {
        raster.clear();
    }
}
