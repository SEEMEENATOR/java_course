package ru.clevertec.check;

public class DiscountCard {
    private int id;
    private String number;
    private int discountPercentage;

    public DiscountCard(int id, String number, int discountPercentage) {
        this.id = id;
        this.number = number;
        this.discountPercentage = discountPercentage;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }
}
