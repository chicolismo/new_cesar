package cesar.gui.tables;

import cesar.hardware.Base;

public class ProgramTableModel extends GenericTableModel {
    private static final long serialVersionUID = -4478787339759105256L;
    private static final String[] columnNames = new String[] { "PC", "Endereço", "Valor", "Mnemônico" };
    private static final Class<?>[] columnClasses = new Class<?>[] { String.class, Integer.class, Byte.class,
        String.class };

    private int currentPcRow;
    private byte[] data;
    private ProgramTable parent;

    public ProgramTableModel(byte[] data) {
        this.currentPcRow = 0;
        this.data = data;
    }

    public void setParent(ProgramTable table) {
        parent = table;
    }

    public int getCurrentPcRow() {
        return currentPcRow;
    }

    public void setCurrentPcRow(int row) {
        if (currentPcRow != row) {
            fireTableRowsUpdated(currentPcRow, currentPcRow);
            fireTableRowsUpdated(row, row);
            currentPcRow = row;
            parent.scrollToRow(row);
        }
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

    private static final String ARROW = "\u279c";
    private static final String EMPTY = "";

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex == currentPcRow ? ARROW : EMPTY;
        }
        else if (getBase() == Base.Decimal) {
            switch (columnIndex) {
                case 1:
                    return Integer.toString(rowIndex);
                case 2:
                    return Integer.toString(0xFF & data[rowIndex]);
                case 3:
                    return "Não implementado";
                default:
                    return EMPTY;
            }
        }
        else {
            switch (columnIndex) {
                case 1:
                    return Integer.toHexString(rowIndex).toUpperCase();
                case 2:
                    return Integer.toHexString(0xFF & data[rowIndex]).toUpperCase();
                case 3:
                    return "Não implementado";
                default:
                    return EMPTY;
            }
        }
    }

    @Override
    public byte getValue(int row) {
        return data[row];
    }
}
