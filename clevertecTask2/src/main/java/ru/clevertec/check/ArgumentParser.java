package ru.clevertec.check;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ArgumentParser {
    private final List<String> args;
    private final Map<Integer, Integer> products = new HashMap<>();
    private String discountCard;
    private double balanceDebitCard;
    private String pathToFile;
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
            } else if (arg.startsWith("pathToFile=")) {
                pathToFile = arg.split("=")[1];
            } else if (arg.startsWith("saveToFile=")) {
                saveToFile = arg.split("=")[1];
            } else {
                String[] parts = arg.split("-");
                int id = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                products.put(id, products.getOrDefault(id, 0) + quantity);
            }
        }
    }

    public void saveToFile(String fullPath) {
        try (FileWriter writer = new FileWriter(fullPath)) {
            writer.write("Discount card: " + discountCard + "\n");
            writer.write("Balance debit card: " + balanceDebitCard + "\n");
            writer.write("Products:\n");
            for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
                writer.write("Product ID: " + entry.getKey() + ", Quantity: " + entry.getValue() + "\n");
            }
            System.out.println("File saved successfully: " + fullPath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + fullPath);
            e.printStackTrace();
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

    public String getPathToFile() {
        return pathToFile;
    }

    public String getSaveToFile() {
        return saveToFile;
    }
}
