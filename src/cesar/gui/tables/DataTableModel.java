package cesar.gui.tables;

import cesar.hardware.Base;

public class DataTableModel extends GenericTableModel {
    private static final long serialVersionUID = -8479421195862323724L;

    private static final String[] columnNames = new String[] { "Endereço", "Valor" };
    private byte[] data;
    private Base currentBase;

    public DataTableModel(byte[] data) {
        this.data = data;
        this.currentBase = Base.Decimal;
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
        if (currentBase == Base.Decimal) {
            if (columnIndex == 0) {
                return Integer.toString(rowIndex);
            }
            else {
                return Integer.toString(0xFF & data[rowIndex]);
            }
        }
        else {
            if (columnIndex == 0) {
                return Integer.toHexString(rowIndex);
            }
            else {
                return Integer.toHexString(0xFF & data[rowIndex]);
            }
        }
    }

    @Override
    public byte getValue(int row) {
        return data[row];
    }

}
