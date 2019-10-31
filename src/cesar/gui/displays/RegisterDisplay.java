package cesar.gui.displays;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cesar.hardware.Base;

public class RegisterDisplay extends JPanel {
    private static final long serialVersionUID = 7050289063551512021L;

    private final DigitalDisplay digitalDisplay;
    private final BinaryDisplay binaryDisplay;
    private Base currentBase;
    private final int registerNumber;

    public RegisterDisplay(final int registerNumber, final String label) {
        super(true);
        this.currentBase = Base.Decimal;
        this.registerNumber = registerNumber;
        this.digitalDisplay = new DigitalDisplay();
        this.binaryDisplay = new BinaryDisplay();

        digitalDisplay.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        binaryDisplay.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalGlue());
        add(digitalDisplay);
        add(Box.createRigidArea(new Dimension(0, 2)));
        add(binaryDisplay);
        add(Box.createVerticalGlue());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), label, TitledBorder.LEFT,
            TitledBorder.CENTER));

        doLayout();
        setMinimumSize(getPreferredSize());
    }

    public void setBase(Base newBase) {
        if (currentBase != newBase) {
            currentBase = newBase;
            digitalDisplay.setBase(newBase);
            digitalDisplay.repaint();
        }
    }
}
