package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class UserDAO {
    Connector connector = new Connector();
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    
    public User login(String username, String pass){
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        User user = new User();
        
        try{
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, pass);
            
            rs = ps.executeQuery();
            
            if(rs.next()){
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setBox(rs.getString("box"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
            }
            
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
        return user;
    }
    
    public boolean register(User user){
        boolean check = true;
        String sql = "INSERT INTO users (username, first_name, last_name, password, box, role) VALUES (?, ?, ?, ?, ?, ?)";
        
        try{
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getBox());
            ps.setString(6, user.getRole());

            ps.execute();
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        }
        finally{
           return check; 
        }
        
        
    }
    
    
}
