package cesar.gui;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import cesar.gui.displays.TextDisplay;

public class TextWindow extends JDialog {
    private static final long serialVersionUID = -594058684805922238L;

    private TextDisplay display;

    public TextWindow(MainWindow parent, byte[] cpuMemory) {
        super(parent, "Visor");
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        display = new TextDisplay(cpuMemory);
        setLayout(new BorderLayout());
        add(display, BorderLayout.CENTER);
        pack();
        setResizable(false);
    }

    @Override
    public void repaint() {
        super.repaint();
        display.repaint();
    }
}
