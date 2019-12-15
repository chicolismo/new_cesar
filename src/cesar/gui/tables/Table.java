package cesar.gui.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import cesar.hardware.Base;

public abstract class Table<Model extends GenericTableModel> extends JTable {
    private static final long serialVersionUID = -7370177993874174121L;

    public Table(Model model) {
        super(model);
        setShowGrid(false);
        setShowVerticalLines(true);
        setGridColor(Color.LIGHT_GRAY);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        setIntercellSpacing(new Dimension(5, 0));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = -1742915757835043616L;
            final private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 10);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(font);
                return this;
            }
        };

        final JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
//        header.setResizingAllowed(false);
        header.setResizingAllowed(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(new Color(0xdddddd));
        headerRenderer.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        header.setDefaultRenderer(headerRenderer);

        final ListSelectionModel selectionModel = getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        updateColumnWidths();
    }

    abstract void updateColumnWidths();

    public byte getValue(int address) {
        return ((GenericTableModel) getModel()).getValue(address);
    }

    public void setBase(Base base) {
        ((GenericTableModel) getModel()).setBase(base);
    }

    public void scrollToRow(int row) {
        scrollToRow(row, false);
    }

    public void scrollToRow(int row, boolean putOnTop) {
        Rectangle rect;
        if (putOnTop) {
            int y = getRowHeight() * row;
            int x = 0;
            int height = getParent().getHeight();
            int width = getWidth();
            rect = new Rectangle(x, y, width, height);
        }
        else {
            rect = getCellRect(row, 0, true);
        }
        scrollRectToVisible(rect);
    }
}
