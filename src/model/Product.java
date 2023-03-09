package model;

public class Product {
    private int id;
    private String name;
    private String description;
    private int stock;
    private double productionCost;
    private double sellingPrice;
    private int categoryId;
    private String status;

    public Product() {
    }

    public Product(int id, String name, String description, int stock, double productionCost, double sellingPrice, int categoryId, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.productionCost = productionCost;
        this.sellingPrice = sellingPrice;
        this.categoryId = categoryId;
        this.status = status;
    }  

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }  

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getProductionCost() {
        return productionCost;
    }

    public void setProductionCost(double productionCost) {
        this.productionCost = productionCost;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
