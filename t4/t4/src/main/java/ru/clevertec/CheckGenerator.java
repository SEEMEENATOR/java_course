package ru.clevertec;

import ru.clevertec.ProductDAO;
import ru.clevertec.Check;
import ru.clevertec.DiscountCard;
import ru.clevertec.Product;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckGenerator {
    private final List<Product> products;
    private final List<DiscountCard> discountCards;
    private final ProductDAO productDAO;

    public CheckGenerator(List<Product> products, List<DiscountCard> discountCards, ProductDAO productDAO) {
        this.products = products;
        this.discountCards = discountCards;
        this.productDAO = productDAO;
    }

    public Check generateCheck(Map<Integer, Integer> productQuantities, String discountCardNumber, double balanceDebitCard) throws SQLException {
        Map<Product, Integer> purchasedProducts = new HashMap<>();
        double totalPrice = 0.0;
        double totalDiscount = 0.0;

        DiscountCard discountCard = discountCards.stream()
                .filter(card -> card.getNumber().equals(discountCardNumber))
                .findFirst()
                .orElse(null);

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = products.stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                if (product.getQuantityInStock() < quantity) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getDescription());
                }

                purchasedProducts.put(product, quantity);
                totalPrice += product.getPrice() * quantity;
                if (discountCard != null) {
                    totalDiscount += product.getPrice() * quantity * discountCard.getDiscountPercentage() / 100;
                }
            } else {
                throw new IllegalArgumentException("Product not found: " + productId);
            }
        }

        double totalWithDiscount = totalPrice - totalDiscount;
        if (balanceDebitCard < totalWithDiscount) {
            throw new IllegalArgumentException("Insufficient balance on debit card.");
        }

        double remainingBalance = balanceDebitCard - totalWithDiscount;

        Check check = new Check(purchasedProducts, discountCard, totalPrice, totalDiscount, remainingBalance);

        for (Map.Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            productDAO.reduceProductQuantity(entry.getKey().getId(), entry.getValue());
        }

        return check;
    }
}
