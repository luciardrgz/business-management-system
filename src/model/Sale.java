package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    private int id;
    private int product; // id to product
    private int quantity;
    private int customer; // id to customer
    private EPaymentMethod paymentMethod;
    private double total;
}
