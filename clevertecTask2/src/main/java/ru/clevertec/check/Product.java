package ru.clevertec.check;

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

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public boolean isWholesale() {
        return wholesale;
    }
}
