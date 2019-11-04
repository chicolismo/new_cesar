package cesar.gui.tables;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class DataTable extends Table<DataTableModel> {
    private static final long serialVersionUID = -5256889056472626825L;

    public DataTable(DataTableModel model) {
        super(model);

        TableColumnModel columnModel = getColumnModel();
        columnModel.setColumnSelectionAllowed(false);

        TableColumn col;
        col = columnModel.getColumn(0);
        col.setPreferredWidth(72);

        col = columnModel.getColumn(1);
        col.setPreferredWidth(46);
    }

}
