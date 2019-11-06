package cesar.gui.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

    private int currentlySelectedRow;
    private final JScrollPane scrollPane;
    Base currentBase;
    private final JLabel label;
    private final JTextField input;
    private final Table<T> table;

    public SideWindow(MainWindow parent, String title, Table<T> table) {
        super(parent, title);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        this.currentlySelectedRow = 0;
        this.currentBase = Base.Decimal;
        this.label = new JLabel("0");
        this.input = new JTextField(5);
        this.table = table;
        this.scrollPane = new JScrollPane(table);

        initLayout();
        pack();
        setSize(0, 0);
        initEvents();
    }

    private void initLayout() {
        JPanel panel = new JPanel();
        setContentPane(panel);
        panel.add(scrollPane, BorderLayout.CENTER);

//        scrollPane.setPreferredSize(table.getPreferredSize());
        scrollPane.setSize(table.getPreferredSize());

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

    public String getLabelText() {
        return label.getText();
    }

    public String getInputText() {
        return input.getText();
    }

    public JTextField getInput() {
        return input;
    }

    public Table<T> getTable() {
        return table;
    }

    private void initEvents() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSize(new Dimension(getPreferredSize().width, getHeight()));
                super.componentResized(e);
            }
        });


        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!selectionModel.getValueIsAdjusting()) {
                    int row = selectionModel.getMaxSelectionIndex();
                    if (row != -1) {
                        currentlySelectedRow = row;
                        updateLabelAndInputValues(row);
                        input.requestFocus();
                        input.selectAll();
                    }
                }
            }
        });
    }

    private void updateLabelAndInputValues(int row) {
        short address = (short) row;
        byte value = table.getValue(row);
        label.setText(Integer.toString(Shorts.toUnsignedInt(address), Base.toInt(currentBase)).toUpperCase());
        input.setText(Integer.toString(Bytes.toUnsignedInt(value), Base.toInt(currentBase)).toUpperCase());
    }

    public void setBase(Base base) {
        if (currentBase != base) {
            currentBase = base;
            table.setBase(base);
            updateLabelAndInputValues(currentlySelectedRow);
        }
    }

    public void selectNextRow() {
        currentlySelectedRow = 0xFFFF & (currentlySelectedRow + 1);
        table.setRowSelectionInterval(currentlySelectedRow, currentlySelectedRow);
        table.scrollToRow(currentlySelectedRow);
    }
}
