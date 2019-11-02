package cesar;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cesar.gui.MainWindow;

public class Main {
    private static void centerWindowOnScreen(MainWindow window) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width / 2 - window.getSize().width / 2, dim.height / 2 - window.getSize().height / 2);
    }

    public static void main(String[] args) {

        try {
            // Set cross-platform Java L&F (also called "Metal")
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
            | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }


        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            centerWindowOnScreen(window);
            window.setVisible(true);
        });
    }
}
