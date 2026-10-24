package view;

import controller.Controller;
import model.objectdata.Solid;
import model.rasterdata.ColorBuffer;
import model.rasterdata.Visibility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class Canvas extends JPanel {
    private final ColorBuffer raster;
    private List<Solid> solids;
    private String currentObjectName = "tetrahedron";
    private String currentProjectionText = "Perspective projection [P]";
    private String currentMode = "Solid model [M]";
    private String currentCameraMode = "3rd person";
    private Solid lightSphere;
    private final JSlider ambientSlider;
    private final JSlider specularSlider;
    private final JSlider diffuseSlider;
    private Visibility visibility;
    private Controller controller;

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

        sliders.add(labeledSlider("Ambient component", ambientSlider));
        sliders.add(labeledSlider("Diffuse component", diffuseSlider));
        sliders.add(labeledSlider("Specular component", specularSlider));

        add(sliders, BorderLayout.NORTH);

        // listener pro resize okna
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (w > 0 && h > 0) {
                    if (visibility != null) {
                        visibility.resize(w, h);
                    }
                    if (controller != null) {
                        controller.redraw();
                    }
                }
            }
        });

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
        this.currentCameraMode = firstPerson ? "1st person" : "3rd person";
        repaint();
    }

    public void setLightSphere(Solid lightSphere) {
        this.lightSphere = lightSphere;
    }

    public void setVisibility(Visibility vB) {
        this.visibility = vB;
    }

    public void setController(Controller controller) {
        this.controller = controller;
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
        g.drawString("Help:", 10, getHeight() - 160);
        g.setColor(Color.WHITE);
        g.drawString("[arrows] move object", 10, getHeight() - 135);
        g.drawString("[R] animate light over time", 10, getHeight() - 110);
        g.drawString("[+] enlarge object, [-] shrink object", 10, getHeight() - 85);
        g.drawString("[1] constant color, [2] interpolated, [3] texture", 10, getHeight() - 60);
        g.drawString("[C] clear, [O] reset, [XYZ] rotate around axis", 10, getHeight() - 35);
        String selectedLabel = "Selected object: ";
        g.drawString(selectedLabel, 10, getHeight() - 10);
        g.setColor(Color.ORANGE);
        g.drawString(currentObjectName, 10 + g.getFontMetrics().stringWidth(selectedLabel), getHeight() - 10);

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
            g.drawString("[L] light", 10, y);
        }

        // pravý dolní roh = model + projekce
        g.setColor(Color.WHITE);
        drawStringRight(g, currentMode, getHeight() - 35);
        drawStringRight(g, currentProjectionText, getHeight() - 10);

        // pravý horní roh = pozorovatel
        g.setColor(Color.ORANGE);
        drawStringRight(g, "Camera:", 25);
        g.setColor(Color.WHITE);
        drawStringRight(g, "[WASD] move", 50);
        drawStringRight(g, "[Q] turn left, [E] turn right", 75);
        drawStringRight(g, "[SHIFT] look up, [CTRL] look down", 100);
        drawStringRight(g, currentCameraMode + " [SPACE]", 125);
    }

    // text zarovnaný k pravému okraji okna (odsazení 10 px)
    private void drawStringRight(Graphics g, String text, int y) {
        g.drawString(text, getWidth() - 10 - g.getFontMetrics().stringWidth(text), y);
    }

    public ColorBuffer getRaster() {
        return raster;
    }

    public void clear() {
        raster.clear();
    }
}
