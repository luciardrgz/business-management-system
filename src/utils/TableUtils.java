package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TableUtils extends DefaultTableCellRenderer {

    private static final Font headerFont = new Font("Montserrat", Font.BOLD, 14);
    private static final Color deletedElementsForeground = Color.decode("#999999");

    public static void setUpTableStyle(JTable table, DefaultTableModel tableModel) {
        table.setRowHeight(30);
        table.setModel(tableModel);
        JTableHeader header = table.getTableHeader();

        header.setFont(headerFont);
        header.setOpaque(false);
        header.setBackground(Color.decode("#47AE69"));
        header.setForeground(Color.white);

        TableCellRenderer renderer = createTableCellRenderer();
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(renderer);
        }
    }

    private static TableCellRenderer createTableCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (lookForDeletedElements(table, row)) {
                    component.setForeground(deletedElementsForeground);
                } else {
                    component.setForeground(Color.black);
                    component.setBackground(Color.white);
                }
                return component;
            }
        };
    }

    public static void clearTable(DefaultTableModel table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.removeRow(i);
            i = i - 1;
        }
    }

    public static void centerTableContent(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private static boolean lookForDeletedElements(JTable table, int row) {
        if (row < 0 || row >= table.getRowCount()) {
            return false;
        }
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
