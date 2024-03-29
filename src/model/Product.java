package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private String name;
    private String description;
    private int stock;
    private double productionCost;
    private double sellingPrice;
    private int categoryId;
    private EProductStatus status;
}
