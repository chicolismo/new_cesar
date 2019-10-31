package cesar.gui.tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class Table<Model extends GenericTableModel> extends JTable {
    private static final long serialVersionUID = -7370177993874174121L;

    public Table(Model model) {
        super(model);
        ListSelectionModel selectionModel = getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionModel(selectionModel);
    }

    public byte getValue(int address) {
        return ((GenericTableModel) getModel()).getValue(address);
    }
}
