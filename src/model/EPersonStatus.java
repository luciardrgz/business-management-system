package model;

public enum EPersonStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),;

    private final String nameForUser;

    EPersonStatus(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static EPersonStatus nameForUserToConstant(String nameForUser) {
        
        for (EPersonStatus status : EPersonStatus.values()) {
            
            if (status.getNameForUser().equals(nameForUser)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }

}
