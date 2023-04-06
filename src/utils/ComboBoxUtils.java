package utils;

import javax.swing.JComboBox;
import model.EPaymentMethod;
import model.EPurchaseStatus;

public class ComboBoxUtils {
    
    public static void loadPaymentMethodsComboBox(JComboBox comboBox) {
        EPaymentMethod[] paymentMethods = EPaymentMethod.class.getEnumConstants();
        comboBox.removeAllItems();
        
        for (EPaymentMethod paymentMethod : paymentMethods) {
            comboBox.addItem(paymentMethod.getNameForUser());
        }
    }

    public static void loadStatusesComboBox(JComboBox comboBox) {
        EPurchaseStatus[] statuses = EPurchaseStatus.class.getEnumConstants();
        comboBox.removeAllItems();
        
        for (EPurchaseStatus status : statuses) {
            comboBox.addItem(status.getNameForUser());
        }
    }
    
    public static String[] splitCboxCustomerName(String fullCustomerName) {
        String[] nameComposition = fullCustomerName.split(" ");
        return nameComposition;
    }
}

