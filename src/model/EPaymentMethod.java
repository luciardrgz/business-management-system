package model;

public enum EPaymentMethod {
    CASH("Efectivo"),
    CDNI("Cuenta DNI"),
    MP("Mercado Pago"),
    CREDIT("Tarjeta de Crédito"),
    DEBIT("Tarjeta de Débito"),
    BANK_TRANSFER("Transferencia Bancaria");

    private final String nameForUser;

    EPaymentMethod(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }

    public static EPaymentMethod nameForUserToConstant(String nameForUser) {
        
        for (EPaymentMethod paymentMethod : EPaymentMethod.values()) {
            
            if (paymentMethod.getNameForUser().equals(nameForUser)) {                
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("Invalid payment method " + nameForUser);
    }
}
