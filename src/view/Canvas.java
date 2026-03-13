package view;

import model.objectdata.Solid;
import model.rasterdata.ColorBuffer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Canvas extends JPanel {
    private final ColorBuffer raster;
    private List<Solid> solids;
    private String currentObjectName = "čtyřstěn";
    private String currentProjectionText = "Perspektivní projekce [P]";
    private String currentMode = "Vyplněný model [M]";
    private String currentCameraMode = "3. osoba [MEZERNÍK] ";
    private Solid lightSphere;
    private final JSlider ambientSlider;

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());

        raster = new ColorBuffer(width, height);

        // slider pro osvětlení nahoře
        ambientSlider = new JSlider(0, 100, 50); // 0–1 => 0–100
        ambientSlider.setMajorTickSpacing(25);
        ambientSlider.setPaintTicks(true);
        ambientSlider.setPaintLabels(true);

        add(ambientSlider, BorderLayout.NORTH);

        clear();
    }

    public void setCurrentProjectionText(String text) {
        this.currentProjectionText = text;
    }

    public void setCurrentMode(String mode) {
        this.currentMode = mode;
    }

    public void setCurrentObject(String name) {
        this.currentObjectName = name;
        repaint();
    }

    public void setObjects(java.util.List<Solid> solids) {
        this.solids = solids;
    }

    public void setCurrentCameraMode(boolean firstPerson) {
        this.currentCameraMode = firstPerson ? "1. osoba" : "3. osoba";
        repaint();
    }

    public void setLightSphere(Solid lightSphere) {
        this.lightSphere = lightSphere;
    }

    public JSlider getAmbientSlider() {
        return ambientSlider;
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

        // světlo = vždy bílé, mimo seznam objektů
        if (lightSphere != null) {
            if (lightSphere.isSelected()) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString("[L] světlo", 10, y);
        }

        // pravý dolní roh = model + projekce
        g.setColor(Color.WHITE);
        g.drawString(currentMode, getWidth() - 160, getHeight() - 35);
        g.drawString(currentProjectionText, getWidth() - 200, getHeight() - 10);

        // pravý horní roh = pozorovatel
        g.setColor(Color.ORANGE);
        g.drawString("Pozorovatel:", getWidth() - 105, 25);
        g.setColor(Color.WHITE);
        g.drawString("[WASD] pohyb", getWidth() - 120, 50);
        g.drawString("[Q] vlevo, [E] vpravo", getWidth() - 160, 75);
        g.drawString("[SHIFT] nahoru, [CTRL] dolů", getWidth() - 225, 100);
        g.drawString(currentCameraMode + " [MEZERNÍK]", getWidth() - 170, 125);
    }

    public ColorBuffer getRaster() {
        return raster;
    }

    public void clear() {
        raster.clear();
    }
}
