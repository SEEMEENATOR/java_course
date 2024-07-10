package ru.clevertec.check;

import java.util.*;

public class ArgumentParser {
    private final List<String> args;
    private final Map<Integer, Integer> products = new HashMap<>();
    private String discountCard;
    private double balanceDebitCard;
    private String datasourceUrl;
    private String datasourceUsername;
    private String datasourcePassword;
    private String saveToFile;

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
            } else if (arg.startsWith("datasource.url=")) {
                datasourceUrl = arg.split("=")[1];
            } else if (arg.startsWith("datasource.username=")) {
                datasourceUsername = arg.split("=")[1];
            } else if (arg.startsWith("datasource.password=")) {
                datasourcePassword = arg.split("=")[1];
            } else if (arg.startsWith("saveToFile=")) {
                saveToFile = arg.split("=")[1];
            } else {
                String[] parts = arg.split("-");
                int productId = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                products.put(productId, quantity);
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

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public String getDatasourceUsername() {
        return datasourceUsername;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
    }

    public String getSaveToFile() {
        return saveToFile;
    }
}
