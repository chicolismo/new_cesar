package cesar.gui.tables;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class DataTable extends Table<DataTableModel> {
    private static final long serialVersionUID = -5256889056472626825L;

    public DataTable(DataTableModel model) {
        super(model);
    }

    @Override
    void updateColumnWidths() {
        TableColumnModel columnModel = getColumnModel();
        columnModel.setColumnSelectionAllowed(false);

        int col_0 = 65;
        int col_1 = 65;

        TableColumn col;
        col = columnModel.getColumn(0);
        col.setMinWidth(col_0);
        col.setMaxWidth(col_0);
        col.setPreferredWidth(col_0);

        col = columnModel.getColumn(1);
        col.setMinWidth(col_1);
        col.setMaxWidth(col_1);
        col.setPreferredWidth(col_1);
    }
}
