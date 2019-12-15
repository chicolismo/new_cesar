package cesar.gui.windows;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import cesar.gui.displays.TextDisplay;

public class TextWindow extends JDialog {
    private static final long serialVersionUID = -594058684805922238L;

    private TextDisplay display;

    public TextWindow(MainWindow parent, byte[] cpuMemory) {
        super(parent, "Visor");
        setType(Window.Type.UTILITY);
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setFocusable(false);
        display = new TextDisplay(cpuMemory);

        final JPanel panel = new JPanel(true);
        setContentPane(panel);
        setLayout(new BorderLayout());
        panel.setLayout(new BorderLayout());
        panel.add(display, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        pack();
        setResizable(false);
        initEvents();
    }

    @Override
    public void repaint() {
        super.repaint();
        display.repaint();
    }

    private void initEvents() {
        final TextWindow textWindow = this;

        final MouseAdapter mouseAdapter = new MouseAdapter() {
            Point clickPoint = null;

            @Override
            public void mousePressed(MouseEvent event) {
                clickPoint = event.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (event.getSource() == textWindow && clickPoint != null) {
                    Point newPoint = event.getLocationOnScreen();
                    newPoint.translate(-clickPoint.x, -clickPoint.y);
                    textWindow.setLocation(newPoint);
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
}
