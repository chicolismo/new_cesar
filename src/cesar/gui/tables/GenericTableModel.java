package cesar.gui.tables;

import javax.swing.table.AbstractTableModel;

import cesar.hardware.Base;

public abstract class GenericTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -5957471466964522858L;
    private Base currentBase;

    public GenericTableModel() {
        currentBase = Base.Decimal;
    }

    public void setBase(Base base) {
        if (currentBase != base) {
            currentBase = base;
            fireTableDataChanged();
        }
    }

    public Base getBase() {
        return currentBase;
    }

    public abstract byte getValue(int row);
}
