package model;

public enum ERole {
    ADMIN("Administrador"),
    HELPER("Ayudante");

    private final String nameForUser;

    ERole(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static ERole nameForUserToConstant(String nameForUser) {
        
        for (ERole role : ERole.values()) {
            
            if (role.getNameForUser().equals(nameForUser)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }

}
