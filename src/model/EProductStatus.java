package model;

public enum EProductStatus {
    AVAILABLE("Disponible"),
    DISCONTINUED("Discontinuado");

    private final String nameForUser;

    EProductStatus(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static EProductStatus nameForUserToConstant(String nameForUser) {
        
        for (EProductStatus status : EProductStatus.values()) {
            
            if (status.getNameForUser().equals(nameForUser)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }

}
