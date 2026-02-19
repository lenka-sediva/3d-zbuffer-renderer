package app;

import controller.Controller;
import view.Window;
import javax.swing.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    Window window = new Window(800, 600);
                    new Controller(window.getCanvas());
                    window.setVisible(true);
                });
    }

}