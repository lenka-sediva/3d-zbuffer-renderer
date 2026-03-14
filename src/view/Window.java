package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private final Canvas panel;

    public Window(int width, int height) {
        panel = new Canvas(width, height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setTitle("PGRF2 2026: Task 1 by Lenka Šedivá");

        add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);

        setLocationRelativeTo(null);

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice
    }

    public Canvas getCanvas() {
        return panel;
    }
}
