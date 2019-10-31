package cesar.gui.tables;

import javax.swing.table.AbstractTableModel;

public abstract class GenericTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -5957471466964522858L;

    public abstract byte getValue(int row);
}
