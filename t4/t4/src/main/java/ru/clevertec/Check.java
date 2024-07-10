package ru.clevertec;

import java.util.Map;

public class Check {
    private Map<Product, Integer> products;
    private DiscountCard discountCard;
    private double totalPrice;
    private double totalDiscount;
    private double totalWithDiscount;
    private double remainingBalance;

    public Check(Map<Product, Integer> products, DiscountCard discountCard, double totalPrice, double totalDiscount, double remainingBalance) {
        this.products = products;
        this.discountCard = discountCard;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
        this.totalWithDiscount = totalPrice - totalDiscount;
        this.remainingBalance = remainingBalance;
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

    public double getRemainingBalance() {
        return remainingBalance;
    }
}
