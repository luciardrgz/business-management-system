package model;

public enum EPurchaseStatus {
    DONE("Realizada"),
    WAITING("En espera"),
    RECEIVED("Recibida"),
    CANCELLED("Cancelada");

    private final String nameForUser;

    EPurchaseStatus(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static EPurchaseStatus nameForUserToConstant(String nameForUser) {
        
        for (EPurchaseStatus status : EPurchaseStatus.values()) {
            
            if (status.getNameForUser().equals(nameForUser)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }

}
