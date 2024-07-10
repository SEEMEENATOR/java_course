package ru.clevertec;

public class Product {
    private int id;
    private String description;
    private double price;
    private int quantityInStock;
    private boolean wholesale;

    public Product(int id, String description, double price, int quantityInStock, boolean wholesale) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.wholesale = wholesale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public boolean isWholesale() {
        return wholesale;
    }

    public void setWholesale(boolean wholesale) {
        this.wholesale = wholesale;
    }
}
