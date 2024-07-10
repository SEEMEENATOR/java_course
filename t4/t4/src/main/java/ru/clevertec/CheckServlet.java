package ru.clevertec;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/check")
public class CheckServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = System.getProperty("datasource.url");
        String username = System.getProperty("datasource.username");
        String password = System.getProperty("datasource.password");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data;
        try {
            data = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid JSON payload: " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            ProductDAO productDAO = new ProductDAO(connection);
            DiscountCardDAO discountCardDAO = new DiscountCardDAO(connection);

            List<Map<String, Integer>> productQuantitiesList = (List<Map<String, Integer>>) data.get("products");
            if (productQuantitiesList == null || productQuantitiesList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Products list is empty or missing");
                return;
            }
            
            Map<Integer, Integer> productQuantities = new HashMap<>();
            for (Map<String, Integer> productQuantityMap : productQuantitiesList) {
                try {
                    int productId = productQuantityMap.get("id");
                    int quantity = productQuantityMap.get("quantity");
                    productQuantities.put(productId, quantity);
                } catch (NumberFormatException | NullPointerException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid product ID or quantity: " + e.getMessage());
                    return;
                }
            }

            String discountCardNumber = (String) data.get("discountCard");
            if (discountCardNumber == null || discountCardNumber.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Discount Card number is missing or empty");
                return;
            }

            double balanceDebitCard;
            try {
                balanceDebitCard = ((Number) data.get("balanceDebitCard")).doubleValue();
            } catch (NullPointerException | ClassCastException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid or missing balanceDebitCard value");
                return;
            }

            List<Product> products = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();
                try {
                    Product product = productDAO.getProductById(productId);
                    if (product == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("Product not found with id: " + productId);
                        return;
                    }
                    products.add(product);
                } catch (SQLException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to retrieve product from database: " + e.getMessage());
                    return;
                }
            }

            List<DiscountCard> discountCards;
            try {
                discountCards = discountCardDAO.getAllDiscountCards();
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to retrieve discount cards from database: " + e.getMessage());
                return;
            }

            CheckGenerator checkGenerator = new CheckGenerator(products, discountCards, productDAO);

            try {
                Check check = checkGenerator.generateCheck(productQuantities, discountCardNumber, balanceDebitCard);

                if (check == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to generate check");
                    return;
                }

                if (check.getTotalWithDiscount() > balanceDebitCard) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Insufficient funds: balanceDebitCard is less than total check amount");
                    return;
                }

                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"result.csv\"");

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("QTY;DESCRIPTION;PRICE;DISCOUNT;TOTAL");

                    for (Map.Entry<Product, Integer> entry : check.getProducts().entrySet()) {
                        Product product = entry.getKey();
                        int quantity = entry.getValue();
                        double price = product.getPrice() * quantity;
                        double discount = check.getDiscountCard() != null ? price * check.getDiscountCard().getDiscountPercentage() / 100 : 0.0;
                        double total = price - discount;
                        writer.printf("%d;%s;%.2f$;%.2f$;%.2f$\n", quantity, product.getDescription(), price, discount, total);
                    }

                    if (check.getDiscountCard() != null) {
                        writer.printf("DISCOUNT CARD;DISCOUNT PERCENTAGE\n%s;%d%%\n",
                                check.getDiscountCard().getNumber(), check.getDiscountCard().getDiscountPercentage());
                    }

                    writer.printf("TOTAL PRICE;TOTAL DISCOUNT;TOTAL WITH DISCOUNT;REMAINING BALANCE\n%.2f$;%.2f$;%.2f$;%.2f$\n",
                            check.getTotalPrice(), check.getTotalDiscount(), check.getTotalWithDiscount(), check.getRemainingBalance());
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("INTERNAL SERVER ERROR: " + e.getMessage());
        }
    }
}
