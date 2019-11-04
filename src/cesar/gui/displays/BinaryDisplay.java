package cesar.gui.displays;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cesar.utils.Shorts;

public class BinaryDisplay extends JPanel {
    private static final long serialVersionUID = -5490249529061282417L;

    private static BufferedImage[] displayImages;
    private static final int IMAGE_WIDTH = 5;
    private static final int IMAGE_HEIGHT = 5;
    private static final int IMAGE_OFFSET = 5;
    private static final int WIDTH = 5 * 16;
    private static final int HEIGHT = 5;
    private static final int START_X = 75;
    private static final int START_Y = 0;
    private static final int BITS = 16;
    private short value;

    static {
        displayImages = new BufferedImage[2];
        try {
            displayImages[0] = ImageIO
                .read(BinaryDisplay.class.getResourceAsStream("/cesar/gui/assets/mini_led_0.png"));
            displayImages[1] = ImageIO
                .read(BinaryDisplay.class.getResourceAsStream("/cesar/gui/assets/mini_led_1.png"));
        }
        catch (IOException e) {
            System.err.println("Erro ao carregar os mini leds");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public BinaryDisplay() {
        super(true);
        Dimension dim = new Dimension(WIDTH, HEIGHT);
        setSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
        value = (short) 0;
    }

    public void setValue(short newValue) {
        if (value != newValue) {
            value = newValue;
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int n = Shorts.toUnsignedInt(value);
        int x = START_X;
        int currentDigit = 0;

        while (n != 0) {
            ++currentDigit;
            int index = n & 1;
            g.drawImage(displayImages[index], x, START_Y, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            x -= IMAGE_OFFSET;
            n >>= 1;
        }

        while (currentDigit < BITS) {
            ++currentDigit;
            g.drawImage(displayImages[0], x, START_Y, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            x -= IMAGE_OFFSET;
        }
    }
}
