package model;

public enum EUnit {
    UNITS("unidades"),
    CENTIMETERS("cm."),
    METERS("mts."),
    GRAMS("grs."),
    KILOGRAMS("kg.");

    private final String nameForUser;

    EUnit(String nameForUser) {
        this.nameForUser = nameForUser;
    }

    public String getNameForUser() {
        return nameForUser;
    }
    
    public static EUnit nameForUserToConstant(String nameForUser) {
        
        for (EUnit unit : EUnit.values()) {
            
            if (unit.getNameForUser().equals(nameForUser)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + nameForUser);
    }
}

