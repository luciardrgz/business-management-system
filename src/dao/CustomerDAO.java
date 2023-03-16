package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomerDAO {
    
    Connector connector = new Connector();
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    
        public boolean register(Customer customer){
        boolean check = true;
        String sql = "INSERT INTO customers (first_name, last_name, phone, address) VALUES (?, ?, ?, ?)";
        
        try{
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());

            ps.execute();
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        }
        finally{
           return check; 
        }  
    }

        public List getCustomersList(String value){
        List<Customer> customersList = new ArrayList();
        
        String sql = "SELECT * FROM customers ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM customers WHERE first_name LIKE '%" + value + "%' OR last_name LIKE '%" + value + "%'";
        
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
                Customer currentCustomer = new Customer();
                currentCustomer.setId(rs.getInt("id"));
                currentCustomer.setFirstName(rs.getString("first_name"));
                currentCustomer.setLastName(rs.getString("last_name"));
                currentCustomer.setPhone(rs.getString("phone"));
                currentCustomer.setAddress(rs.getString("address"));
                currentCustomer.setStatus(rs.getString("status"));
                
                customersList.add(currentCustomer);
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
        return customersList;
    } 
    
    public boolean update(Customer customer){
        boolean check = true;
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, phone = ?, address = ? WHERE id = ?";
        
        try{
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getId());
            
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
        String sql = "UPDATE customers SET status = ? WHERE id = ?";
        
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
