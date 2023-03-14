package exceptions;

import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBException extends SQLException {

    private String message;

    public DBException(SQLException ex) {
        message = switch (ex.getErrorCode()) {
            case 1062 ->
                "Error: ID duplicado.";
            case 4025 ->
                "El valor ingresado no es válido.";
            case 1451 ->
                "No es posible borrar o modificar este registro, tiene datos relacionados.";
            default ->
                "Error en la base de datos.";
        };
    }

}
