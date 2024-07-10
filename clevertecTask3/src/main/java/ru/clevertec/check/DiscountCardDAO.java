package ru.clevertec.check;

import ru.clevertec.check.DiscountCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscountCardDAO {
    private final Connection connection;

    public DiscountCardDAO(Connection connection) {
        this.connection = connection;
    }

    public List<DiscountCard> getAllDiscountCards() throws SQLException {
        List<DiscountCard> discountCards = new ArrayList<>();
        String query = "SELECT id, number, discount_percentage FROM discount_card";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DiscountCard discountCard = new DiscountCard(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getInt("discount_percentage")
                );
                discountCards.add(discountCard);
            }
        }
        return discountCards;
    }
}
