package model;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import views.AdminPanel;

public class Table extends DefaultTableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, col);

        if(table.getValueAt(row,col).toString().equals("Inactivo")){
            setBackground(Color.red);
            setForeground(Color.white);
        }else{
            setBackground(Color.white);
            setForeground(Color.black);
        }
        
        return this;
    }
}
