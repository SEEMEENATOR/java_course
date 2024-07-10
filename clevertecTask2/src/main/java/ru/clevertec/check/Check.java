package ru.clevertec.check;

import java.util.Map;

public class Check {
    private Map<Product, Integer> products;
    private DiscountCard discountCard;
    private double totalPrice;
    private double totalDiscount;
    private double totalWithDiscount;

    public Check(Map<Product, Integer> products, DiscountCard discountCard, double totalPrice, double totalDiscount, double totalWithDiscount) {
        this.products = products;
        this.discountCard = discountCard;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
        this.totalWithDiscount = totalWithDiscount;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public DiscountCard getDiscountCard() {
        return discountCard;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public double getTotalWithDiscount() {
        return totalWithDiscount;
    }
}
