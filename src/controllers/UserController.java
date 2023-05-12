package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.User;
import dao.UserDAO;
import exceptions.DBException;
import model.EPersonStatus;
import model.ERole;
import repositories.UserRepository;
import utils.TableUtils;
import views.AdminPanel;

public class UserController implements ActionListener, MouseListener, KeyListener {

    private final User user;
    private final UserDAO userDAO;
    private final AdminPanel adminView;
    private final UserRepository userRepository = new UserRepository();
    private DefaultTableModel usersTable = new DefaultTableModel();

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

        ERole role = ERole.nameForUserToConstant(adminView.cbxUserRole.getSelectedItem().toString());
        user.setRole(role);
    }

    private void resetView() {
        TableUtils.clearTable(usersTable);
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
                userRepository.register(user);
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
                userRepository.update(user);
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
                userRepository.changeStatus(EPersonStatus.INACTIVE, id);
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
                userRepository.changeStatus(EPersonStatus.ACTIVE, id);
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
        adminView.usersTable.setDefaultRenderer(adminView.usersTable.getColumnClass(0),  new TableUtils());

        try {
            List<User> usersList = userRepository.getUsersList(adminView.inputUserSearch.getText());
            usersTable = (DefaultTableModel) adminView.usersTable.getModel();
            usersTable.setRowCount(0);
            TableUtils.centerTableContent(adminView.usersTable);
            usersListToObjectArray(usersList);

            TableUtils.setUpTableStyle(adminView.usersTable, usersTable);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void usersListToObjectArray(List<User> usersList) {
        Object[] currentUser = new Object[6];
        for (int i = 0; i < usersList.size(); i++) {
            currentUser[0] = usersList.get(i).getId();
            currentUser[1] = usersList.get(i).getUsername();
            currentUser[2] = usersList.get(i).getFirstName();
            currentUser[3] = usersList.get(i).getLastName();

            Enum currentRoleEnum = usersList.get(i).getRole();
            ERole currentRole = ERole.valueOf(currentRoleEnum.name());
            currentUser[4] = currentRole.getNameForUser();

            currentUser[5] = usersList.get(i).getStatus().getNameForUser();

            usersTable.addRow(currentUser);
        }
    }

    private void clearUsersInput() {
        adminView.inputUserId.setText("");
        adminView.inputUserName.setText("");
        adminView.inputUserFirstName.setText("");
        adminView.inputUserLastName.setText("");
        adminView.inputUserPass.setText("");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.usersTable) {
            int row = adminView.usersTable.rowAtPoint(e.getPoint());

            adminView.inputUserId.setText(adminView.usersTable.getValueAt(row, 0).toString());
            adminView.inputUserName.setText(adminView.usersTable.getValueAt(row, 1).toString());
            adminView.inputUserFirstName.setText(adminView.usersTable.getValueAt(row, 2).toString());
            adminView.inputUserLastName.setText(adminView.usersTable.getValueAt(row, 3).toString());
            setRoleIndex(adminView.usersTable.getValueAt(row, 4).toString());

            adminView.inputUserPass.setEnabled(false);
            adminView.btnRegisterUser.setEnabled(false);
        }
        if (e.getSource() == adminView.btnNewUser) {
            adminView.inputUserPass.setEnabled(true);
            adminView.btnRegisterUser.setEnabled(true);
        }
    }

    private void setRoleIndex(String role) {
        ERole[] roles = ERole.values();
        int roleIndex = -1;
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].getNameForUser().equals(role)) {
                roleIndex = i;
                break;
            }
        }
        if (roleIndex != -1) {
            adminView.cbxUserRole.setSelectedIndex(roleIndex);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputUserSearch) {
            resetView();
        }
    }

    private void loadRolesComboBox() {
        ERole[] roles = ERole.class.getEnumConstants();
        adminView.cbxUserRole.removeAllItems();
        
        for (ERole role : roles) {
            adminView.cbxUserRole.addItem(role.getNameForUser());
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
