package controllers;

import dao.RoleDAO;
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
import views.Table;
import model.User;
import dao.UserDAO;
import exceptions.DBException;
import views.AdminPanel;

public class UserController implements ActionListener, MouseListener, KeyListener {

    private final User user;
    private final UserDAO userDAO;
    private final AdminPanel adminView;
    private DefaultTableModel usersTable = new DefaultTableModel();
    private final RoleDAO roleDAO = new RoleDAO();

    public UserController(User user, UserDAO userDAO, AdminPanel adminView) {
        this.user = user;
        this.userDAO = userDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterUser.addActionListener(this);
        this.adminView.btnUpdateUser.addActionListener(this);
        this.adminView.jMenuItemDeleteUser.addActionListener(this);
        this.adminView.jMenuReenterUser.addActionListener(this);
        this.adminView.btnNewUser.addActionListener(this);
        this.adminView.btnNewUser.addMouseListener(this);
        this.adminView.inputUserSearch.addKeyListener(this);
        this.adminView.usersTable.addMouseListener(this);

        loadRolesComboBox();
        listUsers();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterUser) {
            registerUser();
        } else if (e.getSource() == adminView.btnUpdateUser) {
            updateUser();
        } else if (e.getSource() == adminView.jMenuItemDeleteUser) {
            deleteUser();
        } else if (e.getSource() == adminView.jMenuReenterUser) {
            recoverUser();
        } else {
            clearUsersInput();
        }
    }

    private void setupUser() {
        user.setUsername(adminView.inputUserName.getText());
        user.setFirstName(adminView.inputUserFirstName.getText());
        user.setLastName(adminView.inputUserLastName.getText());

        String roleName = adminView.cbxUserRole.getSelectedItem().toString();
        try {
            int roleId = roleDAO.retrieveRoleIdByName(roleName);
            user.setRole(roleId);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void resetView() {
        clearUsersTable();
        listUsers();
        clearUsersInput();
    }

    private boolean checkNullFields() {
        boolean check = true;

        if (adminView.inputUserName.getText().equals("")
                || adminView.inputUserFirstName.getText().equals("")
                || adminView.inputUserLastName.getText().equals("")) {
            check = false;
        }

        return check;
    }

    private void registerUser() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupUser();
            user.setPassword(String.valueOf(adminView.inputUserPass.getPassword()));

            try {
                userDAO.register(user);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Usuario registrado con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

        }
    }

    private void updateUser() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupUser();
            user.setId(Integer.parseInt((adminView.inputUserId.getText())));

            try {
                userDAO.update(user);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Usuario modificado con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void deleteUser() {
        if (!adminView.inputUserId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputUserId.getText());

            try {
                userDAO.changeStatus("Inactivo", id);
                resetView();
                JOptionPane.showMessageDialog(null, "Usuario dado de baja exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un usuario para darlo de baja.");
        }
    }

    private void recoverUser() {
        if (!adminView.inputUserId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputUserId.getText());

            try {
                userDAO.changeStatus("Activo", id);
                resetView();
                JOptionPane.showMessageDialog(null, "Usuario dado de alta exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un usuario para darlo de alta.");
        }
    }

    private void listUsers() {
        Table color = new Table();
        adminView.usersTable.setDefaultRenderer(adminView.usersTable.getColumnClass(0), color);

        try {
            List<User> usersList = userDAO.getUsersList(adminView.inputUserSearch.getText());
            usersTable = (DefaultTableModel) adminView.usersTable.getModel();

            usersTable.setRowCount(0);

            Object[] currentUser = new Object[6];
            for (int i = 0; i < usersList.size(); i++) {
                currentUser[0] = usersList.get(i).getId();
                currentUser[1] = usersList.get(i).getUsername();
                currentUser[2] = usersList.get(i).getFirstName();
                currentUser[3] = usersList.get(i).getLastName();
                currentUser[4] = roleDAO.retrieveRoleNameById(usersList.get(i).getRole());
                currentUser[5] = usersList.get(i).getStatus();

                usersTable.addRow(currentUser);
            }

            adminView.usersTable.setModel(usersTable);
            JTableHeader header = adminView.usersTable.getTableHeader();
            color.changeHeaderColors(header);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void clearUsersInput() {
        adminView.inputUserId.setText("");
        adminView.inputUserName.setText("");
        adminView.inputUserFirstName.setText("");
        adminView.inputUserLastName.setText("");
        adminView.inputUserPass.setText("");
    }

    private void clearUsersTable() {
        for (int i = 0; i < usersTable.getRowCount(); i++) {
            usersTable.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.usersTable) {
            int row = adminView.usersTable.rowAtPoint(e.getPoint());

            adminView.inputUserId.setText(adminView.usersTable.getValueAt(row, 0).toString());
            adminView.inputUserName.setText(adminView.usersTable.getValueAt(row, 1).toString());
            adminView.inputUserFirstName.setText(adminView.usersTable.getValueAt(row, 2).toString());
            adminView.inputUserLastName.setText(adminView.usersTable.getValueAt(row, 3).toString());
            setCboxRole(row);

            adminView.inputUserPass.setEnabled(false);
            adminView.btnRegisterUser.setEnabled(false);
        }
        if (e.getSource() == adminView.btnNewUser) {
            adminView.inputUserPass.setEnabled(true);
            adminView.btnRegisterUser.setEnabled(true);
        }
    }

    private void setCboxRole(int row) {
        int index;
        try {
            index = roleDAO.retrieveRoleIdByName(adminView.usersTable.getValueAt(row, 4).toString());
            
            if ((index - 1) < adminView.cbxUserRole.getItemCount()) {
                adminView.cbxUserRole.setSelectedIndex(index - 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputUserSearch) {
            clearUsersTable();
            listUsers();
        }
    }

    private void loadRolesComboBox() {
        List<String> roles;
        try {
            roles = roleDAO.getRolesNamesList();
            adminView.cbxUserRole.removeAllItems();
            for (String role : roles) {
                adminView.cbxUserRole.addItem(role);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
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
}
