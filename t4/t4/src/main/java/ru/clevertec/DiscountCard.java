package ru.clevertec;

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
    public void setId(int id) {
        this.id = id;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    public String getNumber() {
        return number;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }
}
