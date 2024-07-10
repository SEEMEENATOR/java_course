package ru.clevertec.check;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckGenerator {
    private final List<Product> productList;
    private final List<DiscountCard> discountCardList;

    public CheckGenerator(List<Product> productList, List<DiscountCard> discountCardList) {
        this.productList = productList;
        this.discountCardList = discountCardList;
    }

    public Check generateCheck(Map<Integer, Integer> productQuantities, String discountCardNumber, double balanceDebitCard) throws IOException {
        Map<Product, Integer> products = new HashMap<>();
        double totalPrice = 0.0;
        double totalDiscount = 0.0;

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int id = entry.getKey();
            int quantity = entry.getValue();
            Product product = findProductById(id);
            if (product == null || product.getQuantityInStock() < quantity) {
                throw new IllegalArgumentException("BAD REQUEST");
            }
            double productPrice = product.getPrice() * quantity;
            double productDiscount = 0.0;

            if (product.isWholesale() && quantity >= 5) {
                productDiscount = productPrice * 0.10;
            } else if (discountCardNumber != null) {
                DiscountCard discountCard = findDiscountCardByNumber(discountCardNumber);
                if (discountCard != null) {
                    productDiscount = productPrice * discountCard.getDiscountPercentage() / 100;
                } else {
                    productDiscount = productPrice * 0.02;
                }
            }

            products.put(product, quantity);
            totalPrice += productPrice;
            totalDiscount += productDiscount;
        }

        double totalWithDiscount = totalPrice - totalDiscount;
        if (balanceDebitCard < totalWithDiscount) {
            throw new IllegalArgumentException("NOT ENOUGH MONEY");
        }

        DiscountCard discountCard = findDiscountCardByNumber(discountCardNumber);
        return new Check(products, discountCard, totalPrice, totalDiscount, totalWithDiscount);
    }

    private Product findProductById(int id) {
        return productList.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private DiscountCard findDiscountCardByNumber(String number) {
        return discountCardList.stream().filter(c -> c.getNumber().equals(number)).findFirst().orElse(null);
    }
    public void printCheckConsole(Check check) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy;HH:mm:ss");
        String date = dateFormat.format(new Date());

        System.out.println(date);
        System.out.println("QTY; DESCRIPTION; PRICE; DISCOUNT; TOTAL");

        for (Map.Entry<Product, Integer> entry : check.getProducts().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double price = product.getPrice() * quantity;
            double discount = check.getDiscountCard() != null ? price * check.getDiscountCard().getDiscountPercentage() / 100 : 0.0;
            double total = price - discount;
            System.out.printf("%d; %s; %.2f$; %.2f$; %.2f$\n", quantity, product.getDescription(), price, discount, total);
        }

        if (check.getDiscountCard() != null) {
            System.out.printf("DISCOUNT CARD; DISCOUNT PERCENTAGE\n%s; %d%%\n",
                    check.getDiscountCard().getNumber(), check.getDiscountCard().getDiscountPercentage());
        }

        System.out.printf("TOTAL PRICE; TOTAL DISCOUNT; TOTAL WITH DISCOUNT\n%.2f$; %.2f$; %.2f$\n",
                check.getTotalPrice(), check.getTotalDiscount(), check.getTotalWithDiscount());
    }

    public void printCheck(Check check, String filePath) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy;HH:mm:ss");
        String date = dateFormat.format(new Date());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(date);
            writer.println("QTY; DESCRIPTION; PRICE; DISCOUNT; TOTAL");

            for (Map.Entry<Product, Integer> entry : check.getProducts().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double price = product.getPrice() * quantity;
                double discount = check.getDiscountCard() != null ? price * check.getDiscountCard().getDiscountPercentage() / 100 : 0.0;
                double total = price - discount;
                writer.printf("%d; %s; %.2f$; %.2f$; %.2f$\n", quantity, product.getDescription(), price, discount, total);
            }

            if (check.getDiscountCard() != null) {
                writer.printf("DISCOUNT CARD; DISCOUNT PERCENTAGE\n%s; %d%%\n",
                        check.getDiscountCard().getNumber(), check.getDiscountCard().getDiscountPercentage());
            }

            writer.printf("TOTAL PRICE; TOTAL DISCOUNT; TOTAL WITH DISCOUNT\n%.2f$; %.2f$; %.2f$\n",
                    check.getTotalPrice(), check.getTotalDiscount(), check.getTotalWithDiscount());
        }

    }
}
