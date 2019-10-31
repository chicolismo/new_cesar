package cesar.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cesar.gui.tables.GenericTableModel;
import cesar.gui.tables.Table;
import cesar.hardware.Base;
import cesar.utils.Bytes;
import cesar.utils.Shorts;

public class SideWindow<T extends GenericTableModel> extends JDialog {
    private static final long serialVersionUID = -8367017002876264050L;

    Base currentBase;
    JLabel label;
    JTextField input;
    Table<T> table;

    public SideWindow(MainWindow parent, String title, Table<T> table) {
        super(parent, title);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        this.currentBase = Base.Decimal;
        this.label = new JLabel("0");
        this.input = new JTextField(10);
        this.table = table;

        initLayout();
        initEvents();
    }

    private void initLayout() {
        JPanel panel = new JPanel();
        setContentPane(panel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(scrollPane.getPreferredSize());

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        inputPanel.add(label);
        inputPanel.add(input);


        GridBagLayout grid = new GridBagLayout();
        grid.columnWidths = new int[] { 0 };
        grid.rowHeights = new int[] { 0, 0 };
        grid.columnWeights = new double[] { 1.0 };
        grid.rowWeights = new double[] { 1.0, 0.0 };

        panel.setLayout(grid);

        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.gridx = 0;
        c_0.gridy = 0;
        c_0.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, c_0);

        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.anchor = GridBagConstraints.EAST;
        c_1.fill = GridBagConstraints.HORIZONTAL;
        c_1.gridx = 0;
        c_1.gridy = 2;
        panel.add(inputPanel, c_1);
        pack();
    }

    private void initEvents() {
        MainWindow parent = (MainWindow) getParent();

        // TODO: Continuar aqui...
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String labelText = label.getText();
                String inputText = input.getText();
                try {
                    final int address = Integer.parseInt(labelText, Base.toInt(currentBase));
                    final byte value = (byte) (0xFF & Integer.parseInt(inputText, Base.toInt(currentBase)));
                    parent.onTextInput(address, value);
                }
                catch (NumberFormatException e) {
                    // Do nothing.
                }
            }
        });

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!selectionModel.getValueIsAdjusting()) {
                    int row = selectionModel.getMaxSelectionIndex();
                    if (row != -1) {
                        short address = (short) row;
                        byte value = table.getValue(row);
                        label.setText(Integer.toString(Shorts.toUnsignedInt(address), Base.toInt(currentBase)));
                        input.setText(Integer.toString(Bytes.toUnsignedInt(value), Base.toInt(currentBase)));
                        input.requestFocus();
                        input.selectAll();
                    }
                }
            }
        });
    }

    public void setBase(Base newBase) {
        currentBase = newBase;
    }
}
