package ru.clevertec.check;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CheckRunner {
    public static void main(String[] args) {
        try {
            ArgumentParser argumentParser = new ArgumentParser(args);

            List<Product> products = CSVReader.readProducts("./src/main/resources/products.csv");
            List<DiscountCard> discountCards = CSVReader.readDiscountCards("./src/main/resources/discountCards.csv");

            CheckGenerator checkGenerator = new CheckGenerator(products, discountCards);

            Map<Integer, Integer> productQuantities = argumentParser.getProducts();
            String discountCardNumber = argumentParser.getDiscountCard();
            double balanceDebitCard = argumentParser.getBalanceDebitCard();

            Check check = checkGenerator.generateCheck(productQuantities, discountCardNumber, balanceDebitCard);

            checkGenerator.printCheck(check, "result.csv");
            checkGenerator.printCheckConsole(check);

        } catch (IllegalArgumentException e) {
            ExceptionHandler.handle("BAD REQUEST: " + e.getMessage()); // Добавлено "BAD REQUEST: "
        } catch (IOException e) {
            ExceptionHandler.handle("INTERNAL SERVER ERROR: " + e.getMessage()); // Добавлено "INTERNAL SERVER ERROR: "
        } catch (Exception e) {
            ExceptionHandler.handle("UNEXPECTED ERROR: " + e.getMessage()); // Обработка любых других неожиданных исключений
        }
    }
}
