package controllers;

import model.User;
import model.UserDAO;
import views.LoginFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import views.AdminPanel;

public class LoginController implements ActionListener {
    private User user;
    private UserDAO userDAO;
    private LoginFrame loginView;

    public LoginController(User user, UserDAO userDAO, LoginFrame loginView) {
        this.user = user;
        this.userDAO = userDAO;
        this.loginView = loginView;
        this.loginView.btnEnter.addActionListener(this);
        this.loginView.btnExit.addActionListener(this);
        this.loginView.setLocationRelativeTo(null);
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent){
        if(actionEvent.getSource() == loginView.btnEnter){
            
           if(loginView.inputUser.getText().equals("") || String.valueOf(loginView.inputPass.getPassword()).equals("")){
                JOptionPane.showMessageDialog(null, "Ingresa ambos campos");
            }else{
                String username = loginView.inputUser.getText();
                String pass = String.valueOf(loginView.inputPass.getPassword());
                user = userDAO.login(username,pass);
                
                if(user.getUsername() != null){
                    AdminPanel adminPanel = new AdminPanel();
                    adminPanel.setVisible(true);
                    this.loginView.dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecta");
                }
            }
        }
        else{
           int exitQuestion = JOptionPane.showConfirmDialog(null, "¿Seguro de que deseas salir?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
           if(exitQuestion == 0){
               System.exit(0);
           }
        }
    }
    
}
