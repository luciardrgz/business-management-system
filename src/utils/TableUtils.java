package utils;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class TableUtils {

    public static void changeHeaderColors(JTable table, DefaultTableModel tableModel) {
        table.setModel(tableModel);
        JTableHeader header = table.getTableHeader();
        
        header.setOpaque(false);
        header.setBackground(Color.decode("#00B359"));
        header.setForeground(Color.white);
    }

    public static void clearTable(DefaultTableModel table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.removeRow(i);
            i = i - 1;
        }
    }

}
