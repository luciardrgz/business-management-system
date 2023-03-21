package model;

public enum EStatus {
    REALIZADA,
    EN_ESPERA,
    RECIBIDA,
    CANCELADA;

    @Override
    public String toString() {
        return name();
    } 
}
