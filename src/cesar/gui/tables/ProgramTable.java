package cesar.gui.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ProgramTable extends Table<ProgramTableModel> {
    private static final long serialVersionUID = -8843361396327035069L;

    public ProgramTable(ProgramTableModel model) {
        super(model);

        DefaultTableCellRenderer firstColumnRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = -5000064972152807203L;
            private final Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(font);
                setForeground(Color.GREEN);
                return this;
            }
        };

        TableColumnModel columnModel = getColumnModel();
        columnModel.setColumnSelectionAllowed(false);

        int col0 = 30;
        int col1 = 68;
        int col2 = 40;
        int col3 = 160;

        TableColumn col;
        col = columnModel.getColumn(0);
        col.setMinWidth(col0);
        col.setMaxWidth(col0);
        col.setPreferredWidth(col0);
        col.setCellRenderer(firstColumnRenderer);

        col = columnModel.getColumn(1);
        col.setMinWidth(col1);
        col.setMaxWidth(col1);
        col.setPreferredWidth(col1);

        col = columnModel.getColumn(2);
        col.setMinWidth(col2);
        col.setMaxWidth(col2);
        col.setPreferredWidth(col2);

        col = columnModel.getColumn(3);
        col.setMinWidth(col3);
        col.setMaxWidth(col3);
        col.setPreferredWidth(col3);
    }
}
