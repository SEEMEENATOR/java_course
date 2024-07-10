package ru.clevertec;

import java.io.*;
import java.util.*;

public class CSVReader {
    public static List<Product> readProducts(String filePath) throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0]);
                String description = values[1];
                double price = Double.parseDouble(values[2]);
                int quantityInStock = Integer.parseInt(values[3]);
                boolean wholesale = values[4].equals("+");
                products.add(new Product(id, description, price, quantityInStock, wholesale));
            }
        }
        return products;
    }

    public static List<DiscountCard> readDiscountCards(String filePath) throws IOException {
        List<DiscountCard> discountCards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0]);
                String number = values[1];
                int discountPercentage = Integer.parseInt(values[2]);
                discountCards.add(new DiscountCard(id, number, discountPercentage));
            }
        }
        return discountCards;
    }
}
