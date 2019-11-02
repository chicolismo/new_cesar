package cesar.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cesar.utils.Defaults;

public class InstructionPanel extends JPanel {
    private static final long serialVersionUID = -7005281883928099202L;

    final private JTextField riTextField;
    final private JTextField mnemTextField;

    public InstructionPanel() {
        riTextField = new JTextField();
        mnemTextField = new JTextField();

        initLayout();
    }

    private void initLayout() {
        JLabel riLabel = Defaults.createLabel("RI: ");
        JLabel mnemLabel = Defaults.createLabel("Mnem: ");

        GridBagLayout grid = new GridBagLayout();
        grid.rowHeights = new int[] { 0, 0 };
        grid.columnWidths = new int[] { 0, 0 };
        grid.rowWeights = new double[] { 0.0, 0.0 };
        grid.columnWeights = new double[] { 0.0, 1.0 };
        setLayout(grid);

        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.gridx = 0;
        c_0.gridy = 0;
        c_0.anchor = GridBagConstraints.WEST;
        add(riLabel, c_0);

        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.gridx = 0;
        c_1.gridy = 1;
        c_1.anchor = GridBagConstraints.WEST;
        add(mnemLabel, c_1);

        GridBagConstraints c_2 = new GridBagConstraints();
        c_2.gridx = 1;
        c_2.gridy = 0;
        c_2.fill = GridBagConstraints.BOTH;
        add(riTextField, c_2);

        GridBagConstraints c_3 = new GridBagConstraints();
        c_3.gridx = 1;
        c_3.gridy = 1;
        c_3.fill = GridBagConstraints.BOTH;
        add(mnemTextField, c_3);

        setBorder(Defaults.createTitledBorder("Instrução:"));
    }
}
