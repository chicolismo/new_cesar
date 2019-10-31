package cesar.gui.tables;

import cesar.hardware.Base;

public class ProgramTableModel extends GenericTableModel {
    private static final long serialVersionUID = -4478787339759105256L;
    private static final String[] columnNames = new String[] { "PC", "Endereço", "Valor", "Mnemônico" };

    private int currentPcRow;
    private Base currentBase;
    private byte[] data;

    public ProgramTableModel(byte[] data) {
        this.currentPcRow = 0;
        this.currentBase = Base.Decimal;
        this.data = data;
    }

    public void setBase(Base newBase) {
        currentBase = newBase;
    }

    public void setCurrentPcRow(int row) {
        currentPcRow = row;
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
            switch (columnIndex) {
                case 0:
                    return rowIndex == currentPcRow ? "->" : "";
                case 1:
                    return Integer.toString(rowIndex);
                case 2:
                    return Integer.toString(0xFF & data[rowIndex]);
                case 3:
                    return "Não implementado";
                default:
                    return "";
            }
        }
        else {
            switch (columnIndex) {
                case 0:
                    return rowIndex == currentPcRow ? "->" : "";
                case 1:
                    return Integer.toHexString(rowIndex);
                case 2:
                    return Integer.toHexString(0xFF & data[rowIndex]);
                case 3:
                    return "Não implementado";
                default:
                    return "";
            }
        }
    }

    @Override
    public byte getValue(int row) {
        return data[row];
    }
}
