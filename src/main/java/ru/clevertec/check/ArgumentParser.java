package ru.clevertec.check;

import java.util.*;

public class ArgumentParser {
    private final List<String> args;
    private final Map<Integer, Integer> products = new HashMap<>();
    private String discountCard;
    private double balanceDebitCard;

    public ArgumentParser(String[] args) {
        this.args = Arrays.asList(args);
        parse();
    }

    private void parse() {
        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                discountCard = arg.split("=")[1];
            } else if (arg.startsWith("balanceDebitCard=")) {
                balanceDebitCard = Double.parseDouble(arg.split("=")[1]);
            } else {
                String[] parts = arg.split("-");
                int id = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                products.put(id, products.getOrDefault(id, 0) + quantity);
            }
        }
    }

    public Map<Integer, Integer> getProducts() {
        return products;
    }

    public String getDiscountCard() {
        return discountCard;
    }

    public double getBalanceDebitCard() {
        return balanceDebitCard;
    }
}
