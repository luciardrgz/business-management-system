package utils;

import javax.swing.JButton;

public class ButtonUtils {

    public static void setUpdateButtonVisible(boolean value, JButton updateButton, JButton registerButton) {
        if (value == true) {
            updateButton.setVisible(true);
            registerButton.setVisible(false);
        } else {
            updateButton.setVisible(false);
            registerButton.setVisible(true);
        }
    }
}
