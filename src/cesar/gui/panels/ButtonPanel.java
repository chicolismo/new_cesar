package cesar.gui.panels;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = -1509965084306287422L;

    private static final BufferedImage runIcon;
    private static final BufferedImage nextIcon;

    static {
        BufferedImage icon = null;
        BufferedImage next = null;
        try {
            icon = ImageIO.read(ButtonPanel.class.getResourceAsStream("/cesar/gui/assets/config.png"));
            next = ImageIO.read(ButtonPanel.class.getResourceAsStream("/cesar/gui/assets/tools.png"));
        }
        catch (IOException e) {
            System.err.println("Erro a ler os ícones dos botões");
            e.printStackTrace();
            System.exit(1);
        }
        runIcon = icon;
        nextIcon = next;
    }

    private final JToggleButton btnDec;
    private final JToggleButton btnHex;
    private final JToggleButton btnRun;
    private final JButton btnNext;

    public ButtonPanel() {
        btnDec = new JToggleButton("0..10");
        btnHex = new JToggleButton("0..F");
        btnRun = new JToggleButton(new ImageIcon(runIcon));
        btnNext = new JButton(new ImageIcon(nextIcon));

        initLayout();
    }

    private void initLayout() {
        BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(box);
        btnDec.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        btnHex.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        btnRun.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        btnNext.setAlignmentY(JComponent.CENTER_ALIGNMENT);

        btnDec.putClientProperty("JButton.buttonType", "segmentedTextured");
        btnDec.putClientProperty("JButton.segmentPosition", "first");
        btnHex.putClientProperty("JButton.buttonType", "segmentedTextured");
        btnHex.putClientProperty("JButton.segmentPosition", "last");

        ButtonGroup g = new ButtonGroup();
        g.add(btnDec);
        g.add(btnHex);

        add(btnDec);
        add(btnHex);
        add(Box.createHorizontalGlue());
        add(btnRun);
        add(btnNext);
    }
}
