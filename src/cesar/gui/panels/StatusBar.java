package cesar.gui.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cesar.utils.Defaults;

public class StatusBar extends JPanel {
    private static final long serialVersionUID = 1408669317780545642L;

    private final JLabel label;

    public StatusBar() {
        super(true);
        label = new JLabel();
        label.setFont(Defaults.DEFAULT_FONT);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(label, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void clear() {
        label.setText("");
    }
}
