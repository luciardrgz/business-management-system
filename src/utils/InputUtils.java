package utils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

public class InputUtils {

    public static void checkNumbersInput(JTextField txtField) {
        txtField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.')) {
                    e.consume();
                }
            }
        });
    }
}
