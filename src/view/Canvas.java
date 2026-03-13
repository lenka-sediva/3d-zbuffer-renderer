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
    private final JSlider specularSlider;
    private final JSlider diffuseSlider;

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());

        raster = new ColorBuffer(width, height);

        // slider pro osvětlení nahoře
        ambientSlider = new JSlider(0, 100, 50); // 0–1 => 0–100
        ambientSlider.setMajorTickSpacing(25);
        ambientSlider.setPaintTicks(true);
        ambientSlider.setPaintLabels(true);
        ambientSlider.setFocusable(false); // aby nezabíral focus a neblokoval klávesové zkratky

        specularSlider = new JSlider(0, 100, 50);
        specularSlider.setMajorTickSpacing(25);
        specularSlider.setPaintTicks(true);
        specularSlider.setPaintLabels(true);
        specularSlider.setFocusable(false);

        diffuseSlider = new JSlider(0, 100, 100); // výchozí 100 = plná difúze
        diffuseSlider.setMajorTickSpacing(25);
        diffuseSlider.setPaintTicks(true);
        diffuseSlider.setPaintLabels(true);
        diffuseSlider.setFocusable(false);

        JPanel sliders = new JPanel();
        sliders.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        sliders.setOpaque(false); // aby nezakrýval celý horní renderovaný obraz

        sliders.add(labeledSlider("Ambientní složka", ambientSlider));
        sliders.add(labeledSlider("Difúzní složka", diffuseSlider));
        sliders.add(labeledSlider("Spekulární složka", specularSlider));

        add(sliders, BorderLayout.NORTH);

        clear();
    }

    private JPanel labeledSlider(String label, JSlider slider) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(l, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);
        slider.setPreferredSize(new Dimension(225, 50));
        return panel;
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

    public JSlider getSpecularSlider() {
        return specularSlider;
    }

    public JSlider getDiffuseSlider() {
        return diffuseSlider;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.present(g);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));

        // levý dolní roh = nápověda
        g.setColor(Color.ORANGE);
        g.drawString("Nápověda:", 10, getHeight() - 160);
        g.setColor(Color.WHITE);
        g.drawString("[šipky] posun objektu", 10, getHeight() - 135);
        g.drawString("[R] animace světla v čase", 10, getHeight() - 110);
        g.drawString("[+] zvětšení objektu, [-] zmenšení objektu", 10, getHeight() - 85);
        g.drawString("[1] konstantní barva, [2] interpolovaná, [3] textura", 10, getHeight() - 60);
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
