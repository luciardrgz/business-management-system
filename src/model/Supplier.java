package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private int id;
    private String firstName;
    private String lastName;
    private String socialName;
    private String cuit;
    private String phone;
    private String address;
    private EPersonStatus status;
}
