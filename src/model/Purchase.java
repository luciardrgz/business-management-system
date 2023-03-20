package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    private int id;
    private String name;
    private String quantity;
    private double unitaryPrice;
    private String date;
    private int supplier;
    private int paymentMethod;
}
