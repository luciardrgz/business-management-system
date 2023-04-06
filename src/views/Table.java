package views;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Table extends DefaultTableCellRenderer {

    private final Color deletedElementsForeground = Color.decode("#999999");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, col);

        if (lookForDeletedElements(table, row)) {
            setForeground(deletedElementsForeground);
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }

        return this;
    }

    private boolean lookForDeletedElements(JTable table, int row) {
        boolean deleted = false;

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getValueAt(row, i) != null
                    && (table.getValueAt(row, i).toString().equals("Discontinuado")
                    || table.getValueAt(row, i).toString().equals("Inactivo"))) {
                deleted = true;
                break;
            }
        }

        return deleted;
    } 
}
