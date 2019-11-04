package cesar.gui.panels;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import cesar.gui.displays.RegisterDisplay;
import cesar.hardware.Base;

public class RegisterPanel extends JPanel {
    private static final long serialVersionUID = 2962079321929645473L;

    final RegisterDisplay[] registerDisplays;

    public RegisterPanel() {
        super(true);

        registerDisplays = new RegisterDisplay[] { new RegisterDisplay(0, "R0:"), new RegisterDisplay(1, "R1:"),
            new RegisterDisplay(2, "R2:"), new RegisterDisplay(3, "R3:"), new RegisterDisplay(4, "R4:"),
            new RegisterDisplay(5, "R5:"), new RegisterDisplay(6, "R6: (SP)"), new RegisterDisplay(7, "R7: (PC)"), };

        GridLayout grid = new GridLayout(3, 3);
        setLayout(grid);
        add(registerDisplays[0]);
        add(registerDisplays[1]);
        add(registerDisplays[2]);
        add(registerDisplays[3]);
        add(registerDisplays[4]);
        add(registerDisplays[5]);
        add(registerDisplays[6]);
        add(Box.createHorizontalGlue());
        add(registerDisplays[7]);
        doLayout();
    }

    public void setBase(Base base) {
        for (final RegisterDisplay display : registerDisplays) {
            display.setBase(base);
        }
    }

    public RegisterDisplay[] getDisplays() {
        return registerDisplays;
    }

    public RegisterDisplay getDisplay(int i) {
        return registerDisplays[i];
    }
}
