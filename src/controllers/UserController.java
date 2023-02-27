package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import model.User;
import model.UserDAO;
import views.AdminPanel;

public class UserController implements ActionListener{
    
    private User user;
    private UserDAO userDAO;
    private AdminPanel adminView;
 
    public UserController(User user, UserDAO userDAO, AdminPanel adminView) {
        this.user = user;
        this.userDAO = userDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterUser.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == adminView.btnRegisterUser){
            if(checkNullFields() == false){
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            }
            else{
                user.setUsername(adminView.inputUserName.getText());
                user.setFirstName(adminView.inputUserFirstName.getText());
                user.setLastName(adminView.inputUserLastName.getText());
                user.setPassword(String.valueOf(adminView.inputUserPass.getPassword()));
                user.setBox(adminView.cbxUserBox.getSelectedItem().toString());
                user.setRole(adminView.cbxUserRole.getSelectedItem().toString());
                
                if(userDAO.register(user)){
                    JOptionPane.showMessageDialog(null, "¡Usuario registrado con éxito!");
                }else{
                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario.");
                }
                
            }
        }
    }
    
    public boolean checkNullFields(){
        boolean check = true;
        
        if(adminView.inputUserName.getText().equals("") 
           || adminView.inputUserFirstName.getText().equals("") 
           || adminView.inputUserLastName.getText().equals("") 
           || String.valueOf(adminView.inputUserPass.getPassword()).equals("")){
        check = false;
        }
        
        return check;
    }
    
}
