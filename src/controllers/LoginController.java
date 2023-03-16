package controllers;

import model.User;
import dao.UserDAO;
import exceptions.DBException;
import views.LoginFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import views.AdminPanel;

public class LoginController implements ActionListener {

    private User user;
    private final UserDAO userDAO;
    private final LoginFrame loginView;

    public LoginController(User user, UserDAO userDAO, LoginFrame loginView) {
        this.user = user;
        this.userDAO = userDAO;
        this.loginView = loginView;
        this.loginView.btnEnter.addActionListener(this);
        this.loginView.btnExit.addActionListener(this);
        this.loginView.getRootPane().setDefaultButton(this.loginView.btnEnter);
        this.loginView.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == loginView.btnEnter) {
            enterButtonAction();
        } else {
            exitButtonAction();
        }
    }

    private void enterButtonAction() {
        if (loginView.inputUser.getText().equals("") || String.valueOf(loginView.inputPass.getPassword()).equals("")) {
            JOptionPane.showMessageDialog(null, "Ingresa ambos campos");
        } else {
            String username = loginView.inputUser.getText();
            String pass = String.valueOf(loginView.inputPass.getPassword());

            try {
                user = userDAO.login(username, pass);
           
                if (user.getUsername() != null) {
                    AdminPanel adminPanel = new AdminPanel();
                    adminPanel.setVisible(true);
                    this.loginView.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecta");
                }
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

        }
    }

    private void exitButtonAction() {
        Object[] exitOptions = new Object[]{"Si", "No"};

        int exitQuestion = JOptionPane.showOptionDialog(null,
                "¿Seguro de que deseas salir?",
                "Salir",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                exitOptions,
                "No");

        if (exitQuestion == 0) {
            System.exit(0);
        }
    }

}
