package model;

public enum ECategoryType {
    PRODUCT("Producto"),
    MATERIAL("Material de trabajo");

    private final String nameForUser;

    ECategoryType(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static ECategoryType nameForUserToConstant(String nameForUser) {
        
        for (ECategoryType status : ECategoryType.values()) {
            
            if (status.getNameForUser().equals(nameForUser)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }

}