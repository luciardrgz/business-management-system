package controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Table;
import model.User;
import model.UserDAO;
import views.AdminPanel;

public class UserController implements ActionListener, MouseListener, KeyListener{
    
    private User user;
    private UserDAO userDAO;
    private AdminPanel adminView;
 
    DefaultTableModel usersTable = new DefaultTableModel();
    
    public UserController(User user, UserDAO userDAO, AdminPanel adminView) {
        this.user = user;
        this.userDAO = userDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterUser.addActionListener(this);
        this.adminView.btnUpdateUser.addActionListener(this);
        this.adminView.jMenuItemDelete.addActionListener(this);
        this.adminView.jMenuReenterUser.addActionListener(this);
        this.adminView.btnNewUser.addActionListener(this);
        this.adminView.inputUserSearch.addKeyListener(this);
        this.adminView.usersTable.addMouseListener(this);
        listUsers();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == adminView.btnRegisterUser){
            registerUser();
        }
        else if(e.getSource() == adminView.btnUpdateUser){
            updateUser();
        }
        else if(e.getSource() == adminView.jMenuItemDelete){            
            deleteUser();
        }
        else if(e.getSource() == adminView.jMenuReenterUser){            
            recoverUser();
        }
        else{
            clearUsersInput();
        }
    }
    
    public boolean checkNullFields(){
        boolean check = true;
        
        if(adminView.inputUserName.getText().equals("") 
           || adminView.inputUserFirstName.getText().equals("") 
           || adminView.inputUserLastName.getText().equals("")){
        check = false;
        }
        
        return check;
    }
    
    public void registerUser(){
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
                    clearUsersTable();
                    listUsers();
                    clearUsersInput();
                    JOptionPane.showMessageDialog(null, "¡Usuario registrado con éxito!");
                }else{
                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario.");
                }
                
            }
    }
    
    public void updateUser(){
        if(checkNullFields() == false){
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            }
            else{
                user.setUsername(adminView.inputUserName.getText());
                user.setFirstName(adminView.inputUserFirstName.getText());
                user.setLastName(adminView.inputUserLastName.getText());
                user.setBox(adminView.cbxUserBox.getSelectedItem().toString());
                user.setRole(adminView.cbxUserRole.getSelectedItem().toString());
                user.setId(Integer.parseInt((adminView.inputUserId.getText())));
                
                if(userDAO.update(user)){
                    clearUsersTable();
                    listUsers();
                    clearUsersInput();
                    JOptionPane.showMessageDialog(null, "¡Usuario modificado con éxito!");
                }else{
                    JOptionPane.showMessageDialog(null, "Error al modificar el usuario.");
                }
                
            }
    }
    
    public void deleteUser(){
        if(!adminView.inputUserId.getText().equals("")){
                int id = Integer.parseInt(adminView.inputUserId.getText());
                if (userDAO.changeStatus("Inactivo", id)){
                    clearUsersTable();
                    listUsers();
                    JOptionPane.showMessageDialog(null, "Usuario dado de baja exitosamente.");
                }
                else{
                     JOptionPane.showMessageDialog(null, "Error al intentar dar de baja al usuario.");
                }
                
                
            }else{
                 JOptionPane.showMessageDialog(null, "Seleccione un usuario para darlo de baja.");
            }
    }
    
    public void recoverUser(){
        if(!adminView.inputUserId.getText().equals("")){
                int id = Integer.parseInt(adminView.inputUserId.getText());
                if (userDAO.changeStatus("Activo", id)){
                    clearUsersTable();
                    listUsers();
                    clearUsersInput();
                    JOptionPane.showMessageDialog(null, "Usuario dado de alta exitosamente.");
                }
                else{
                     JOptionPane.showMessageDialog(null, "Error al intentar dar de alta usuario.");
                }
                
                
            }else{
                 JOptionPane.showMessageDialog(null, "Seleccione un usuario para darlo de alta.");
            }
    }

    public void listUsers(){
     Table color = new Table();
     adminView.usersTable.setDefaultRenderer(adminView.usersTable.getColumnClass(0), color);
     
     List<User> usersList = userDAO.getUsersList(adminView.inputUserSearch.getText());
     usersTable = (DefaultTableModel) adminView.usersTable.getModel();
     
     usersTable.setRowCount(0);
     
     Object[] currentUser = new Object[7];
        for (int i = 0; i < usersList.size(); i++) {
            currentUser[0] = usersList.get(i).getId();
            currentUser[1] = usersList.get(i).getUsername();
            currentUser[2] = usersList.get(i).getFirstName();
            currentUser[3] = usersList.get(i).getLastName();
            currentUser[4] = usersList.get(i).getBox();
            currentUser[5] = usersList.get(i).getRole();
            currentUser[6] = usersList.get(i).getStatus();  
            
            usersTable.addRow(currentUser);
        }
        
        adminView.usersTable.setModel(usersTable);
        JTableHeader header = adminView.usersTable.getTableHeader();
        header.setOpaque(false);
        header.setBackground(Color.blue);
        header.setForeground(Color.white);
    }
    
     private void clearUsersInput(){
         adminView.inputUserId.setText("");
        adminView.inputUserName.setText("");
        adminView.inputUserFirstName.setText("");
        adminView.inputUserLastName.setText("");
        adminView.inputUserPass.setText("");
    }
    
    public void clearUsersTable(){
        for (int i = 0; i < usersTable.getRowCount(); i++){
            usersTable.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == adminView.usersTable){
            int row = adminView.usersTable.rowAtPoint(e.getPoint());
            
            adminView.inputUserId.setText(adminView.usersTable.getValueAt(row, 0).toString());
            adminView.inputUserName.setText(adminView.usersTable.getValueAt(row, 1).toString());
            adminView.inputUserFirstName.setText(adminView.usersTable.getValueAt(row, 2).toString());
            adminView.inputUserLastName.setText(adminView.usersTable.getValueAt(row, 3).toString());
            adminView.cbxUserBox.setSelectedItem(adminView.usersTable.getValueAt(row, 4).toString());
            adminView.cbxUserRole.setSelectedItem(adminView.usersTable.getValueAt(row, 5).toString());
            
            adminView.inputUserPass.setEnabled(false);
            adminView.btnRegisterUser.setEnabled(false);
            
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputUserSearch){
            clearUsersTable();
            listUsers();
        }
    }
   
}
