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
    private static final DefaultTableCellRenderer firstColumnRenderer;

    static {
        firstColumnRenderer = new DefaultTableCellRenderer() {
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
    }

    public ProgramTable(ProgramTableModel model) {
        super(model);
    }

    @Override
    void updateColumnWidths() {
        TableColumnModel columnModel = getColumnModel();
        columnModel.setColumnSelectionAllowed(false);

        int col_0 = 30;
        int col_1 = 68;
        int col_2 = 40;
        int col_3 = 160;

        TableColumn col;
        col = columnModel.getColumn(0);
        col.setMinWidth(col_0);
        col.setMaxWidth(col_0);
        col.setPreferredWidth(col_0);
        col.setCellRenderer(firstColumnRenderer);

        col = columnModel.getColumn(1);
        col.setMinWidth(col_1);
        col.setMaxWidth(col_1);
        col.setPreferredWidth(col_1);

        col = columnModel.getColumn(2);
        col.setMinWidth(col_2);
        col.setMaxWidth(col_2);
        col.setPreferredWidth(col_2);

        col = columnModel.getColumn(3);
        col.setMinWidth(col_3);
        col.setMaxWidth(col_3);
        col.setPreferredWidth(col_3);
    }
}
