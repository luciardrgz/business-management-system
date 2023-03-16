package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class SupplierDAO {
    
    Connector connector = new Connector();
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    
        public boolean register(Supplier supplier){
        boolean check = true;
        String sql = "INSERT INTO suppliers (first_name, last_name, social_name, cuit, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        
        try{
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, supplier.getFirstName());
            ps.setString(2, supplier.getLastName());
            ps.setString(3, supplier.getSocialName());
            ps.setString(4, supplier.getCuit());
            ps.setString(5, supplier.getPhone());
            ps.setString(6, supplier.getAddress());
            ps.execute();
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        }
        finally{
           return check; 
        }  
    }

        public List getSuppliersList(String value){
        List<Supplier> suppliersList = new ArrayList();
        
        String sql = "SELECT * FROM suppliers ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM suppliers WHERE first_name LIKE '%" + value + "%' OR last_name LIKE '%" + value + "%' OR social_name LIKE '%" + value + "%'";
        
        try{
            conn = connector.getConn();
            
            if(value.equalsIgnoreCase("")){
             ps = conn.prepareStatement(sql);
             rs = ps.executeQuery(); 
            } else {
             ps = conn.prepareStatement(valueToSearch);
             rs = ps.executeQuery();
            }
 
            while(rs.next()){
                Supplier currentSupplier = new Supplier();
                currentSupplier.setId(rs.getInt("id"));
                currentSupplier.setFirstName(rs.getString("first_name"));
                currentSupplier.setLastName(rs.getString("last_name"));
                currentSupplier.setSocialName(rs.getString("social_name"));
                currentSupplier.setCuit(rs.getString("cuit"));
                currentSupplier.setPhone(rs.getString("phone"));
                currentSupplier.setAddress(rs.getString("address"));
                currentSupplier.setStatus(rs.getString("status"));
                
                suppliersList.add(currentSupplier);
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
        return suppliersList;
    } 
    
    public boolean update(Supplier supplier){
        boolean check = true;
        String sql = "UPDATE suppliers SET first_name = ?, last_name = ?, social_name = ?, cuit = ?, phone = ?, address = ? WHERE id = ?";
        
        try{
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, supplier.getFirstName());
            ps.setString(2, supplier.getLastName());
            ps.setString(3, supplier.getSocialName());
            ps.setString(4, supplier.getCuit());
            ps.setString(5, supplier.getPhone());
            ps.setString(6, supplier.getAddress());
            ps.setInt(7, supplier.getId());
            
            ps.execute();
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        }
        finally{
           return check; 
        }  
    }
    
    public boolean changeStatus(String status, int id){
         boolean check = true;
        String sql = "UPDATE suppliers SET status = ? WHERE id = ?";
        
        try{
          conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, status);
            ps.setInt(2, id);
            
            ps.execute();  
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        } finally{
           return check; 
        }  
        
            
    }
    
}
