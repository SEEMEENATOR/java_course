package ru.clevertec.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.clevertec.check.ProductDAO;
import ru.clevertec.check.DiscountCardDAO;
import ru.clevertec.check.Product;
import ru.clevertec.check.DiscountCard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckRunner {

    private static final Logger logger = LoggerFactory.getLogger("ru.clevertec.check.CheckRunner");

    public static void main(String[] args) {
        String saveToFile = null;

        try {
            ArgumentParser argumentParser = new ArgumentParser(args);

            String url = argumentParser.getDatasourceUrl();
            String username = argumentParser.getDatasourceUsername();
            String password = argumentParser.getDatasourcePassword();
            saveToFile = argumentParser.getSaveToFile();

            if (url == null || username == null || password == null) {
                handleError("BAD REQUEST: Database connection parameters are required", saveToFile);
                return;
            }

            if (saveToFile == null) {
                saveToFile = "./result.csv";
            }

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                ProductDAO productDAO = new ProductDAO(connection);
                DiscountCardDAO discountCardDAO = new DiscountCardDAO(connection);

                List<Product> products = productDAO.getAllProducts();
                List<DiscountCard> discountCards = discountCardDAO.getAllDiscountCards();

                CheckGenerator checkGenerator = new CheckGenerator(products, discountCards);

                Map<Integer, Integer> productQuantities = argumentParser.getProducts();
                String discountCardNumber = argumentParser.getDiscountCard();
                double balanceDebitCard = argumentParser.getBalanceDebitCard();

                Check check = checkGenerator.generateCheck(productQuantities, discountCardNumber, balanceDebitCard);

                checkGenerator.printCheck(check, saveToFile);
                System.out.println("Check generated successfully.");

                printCheckToConsole(check);
            }
            logger.info("Application started successfully.");

        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException occurred: {}", e.getMessage());
            handleError(e.getMessage(), saveToFile);
        } catch (SQLException e) {
            logger.error("SQLException occurred: {}", e.getMessage());
            handleError("INTERNAL SERVER ERROR: " + e.getMessage(), saveToFile);
        } catch (IOException e) {
            logger.error("IOException occurred: {}", e.getMessage());
            handleError("INTERNAL SERVER ERROR: " + e.getMessage(), saveToFile);
        }
    }

    public static void handleError(String message, String saveToFile) {
        if (saveToFile == null) {
            saveToFile = "./error.log";
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveToFile))) {
            writer.println(message);
            System.err.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write error message to file: " + e.getMessage());
        }
    }

    public static void printCheckToConsole(Check check) {
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
