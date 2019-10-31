package cesar.gui.panels;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class ConditionPanel extends JLabel {
    private static final long serialVersionUID = -595687570078206074L;

    final LedPanel negative;
    final LedPanel zero;
    final LedPanel overflow;
    final LedPanel carry;

    public ConditionPanel() {
        negative = new LedPanel("N");
        zero = new LedPanel("Z");
        overflow = new LedPanel("V");
        carry = new LedPanel("C");

        BoxLayout vbox = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(vbox);
        add(negative);
        add(zero);
        add(overflow);
        add(carry);
    }
}
