package model;

public enum EStatus {
    DONE("Realizada"),
    WAITING("En espera"),
    RECEIVED("Recibida"),
    CANCELLED("Cancelada");

    private final String nameForUser;

    EStatus(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static EStatus nameForUserToConstant(String nameForUser) {
        
        for (EStatus status : EStatus.values()) {
            
            if (status.getNameForUser().equals(nameForUser)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }

}
