package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Connector {
    Connection connection;
    
    public Connection getConn(){
        try{
            String db = "jdbc:mysql://localhost:3306/management_sys";
            connection = DriverManager.getConnection(db, "root", "");
            return connection;
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
        return null;
    }
    
}
