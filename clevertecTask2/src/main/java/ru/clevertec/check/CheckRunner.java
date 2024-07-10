package ru.clevertec.check;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CheckRunner {
    public static void main(String[] args) {
        ArgumentParser parser = new ArgumentParser(args);

        String pathToFile = parser.getPathToFile();
        String saveToFile = parser.getSaveToFile();

        String fullPath = Paths.get(pathToFile, saveToFile).toAbsolutePath().toString();

        System.out.println("pathToFile: " + pathToFile);
        System.out.println("saveToFile: " + fullPath);

        try {
            if (pathToFile == null || pathToFile.isEmpty()) {
                handleError("BAD REQUEST: pathToFile is required", fullPath);
                return;
            }

            if (saveToFile == null || saveToFile.isEmpty()) {
                fullPath = Paths.get(pathToFile, "result.csv").toAbsolutePath().toString(); // Сохранение в result.csv по умолчанию
            }

            List<Product> products = CSVReader.readProducts("./src/main/resources/products.csv");
            List<DiscountCard> discountCards = CSVReader.readDiscountCards("./src/main/resources/discountCards.csv");

            CheckGenerator checkGenerator = new CheckGenerator(products, discountCards);

            Map<Integer, Integer> productQuantities = parser.getProducts();
            String discountCardNumber = parser.getDiscountCard();
            double balanceDebitCard = parser.getBalanceDebitCard();

            Check check = checkGenerator.generateCheck(productQuantities, discountCardNumber, balanceDebitCard);

            checkGenerator.printCheck(check, fullPath);
            System.out.println("Check generated successfully.");

            printCheckToConsole(check);

        } catch (IllegalArgumentException e) {
            handleError(e.getMessage(), fullPath);
        } catch (IOException e) {
            handleError("INTERNAL SERVER ERROR", fullPath);
        }
    }

    private static void handleError(String message, String saveToFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveToFile))) {
            writer.println(message);
            System.err.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write error message to file: " + e.getMessage());
        }
    }

    private static void printCheckToConsole(Check check) {
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
}
