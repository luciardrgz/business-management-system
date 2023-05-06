package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private String socialName;
    private String CUIT;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String extras;

    @Override
    public String toString() {
        return "Razón social: " + socialName + "\nCUIT: " + CUIT + "\nDueño: " + firstName + " " + lastName + "\nTeléfono: " + phone + "\nDirección: " + address + "\nExtras: " + extras;
    }
}
