package cesar.gui.panels;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ConditionPanel extends JPanel {
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
        add(Box.createHorizontalGlue());
        add(zero);
        add(Box.createHorizontalGlue());
        add(overflow);
        add(Box.createHorizontalGlue());
        add(carry);

//        setLayout(vbox);
//        add(negative);
//        add(Box.createHorizontalGlue());
//        add(zero);
//        add(Box.createHorizontalGlue());
//        add(overflow);
//        add(Box.createHorizontalGlue());
//        add(carry);
    }
}
