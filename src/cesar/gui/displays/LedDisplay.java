package cesar.gui.displays;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LedDisplay extends JPanel {
    private static final long serialVersionUID = 7159709799229150768L;

    private static final int WIDTH = 15;
    private static final int HEIGHT = 15;
    private static final BufferedImage[] images;
    private boolean isTurnedOn;

    static {
        images = new BufferedImage[2];
        try {
            images[0] = ImageIO.read(LedDisplay.class.getResourceAsStream("/cesar/gui/assets/light_off.png"));
            images[1] = ImageIO.read(LedDisplay.class.getResourceAsStream("/cesar/gui/assets/light_on.png"));
        }
        catch (IOException e) {
            System.err.println("Erro ao ler as imagens do LED.");
            e.printStackTrace();
        }
    }

    public LedDisplay() {
        super(true);
        Dimension dim = new Dimension(WIDTH, HEIGHT);
        setSize(dim);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
        isTurnedOn = false;
    }

    public void setTurnedOn(boolean value) {
        if (isTurnedOn != value) {
            isTurnedOn = value;
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(isTurnedOn ? images[1] : images[0], 0, 0, WIDTH, HEIGHT, null);
    }

}
