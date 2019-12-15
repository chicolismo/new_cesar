package cesar.gui.tables;

import cesar.hardware.Base;

public class DataTableModel extends GenericTableModel {
    private static final long serialVersionUID = -8479421195862323724L;
    private Class<?>[] columnClasses = new Class<?>[] { Integer.class, Integer.class };

    private static final String[] columnNames = new String[] { "Endereço", "Valor" };
    private byte[] data;

    public DataTableModel(byte[] data) {
        this.data = data;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (getBase() == Base.Decimal) {
            if (columnIndex == 0) {
                return Integer.toString(rowIndex);
            }
            else {
                return Integer.toString(0xFF & data[rowIndex]);
            }
        }
        else {
            if (columnIndex == 0) {
                return Integer.toHexString(rowIndex).toUpperCase();
            }
            else {
                return Integer.toHexString(0xFF & data[rowIndex]).toUpperCase();
            }
        }
    }

    @Override
    public byte getValue(int row) {
        return data[row];
    }
}
