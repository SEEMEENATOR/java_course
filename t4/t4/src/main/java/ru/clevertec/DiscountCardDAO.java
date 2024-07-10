package ru.clevertec;

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
    public void updateDiscountCard(DiscountCard discountCard) throws SQLException {
        String query = "UPDATE discount_card SET number = ?, discount_percentage = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, discountCard.getNumber());
            stmt.setInt(2, discountCard.getDiscountPercentage());
            stmt.setInt(3, discountCard.getId());
            stmt.executeUpdate();
        }
    }
    public void addDiscountCard(DiscountCard discountCard) throws SQLException {
        String query = "INSERT INTO discount_card (number, discount_percentage) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, discountCard.getNumber());
            statement.setInt(2, discountCard.getDiscountPercentage());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                discountCard.setId(generatedKeys.getInt(1));
            }
        }
    }
    public DiscountCard getDiscountCardById(int discountCardId) throws SQLException {
        String query = "SELECT id, number, discount_percentage FROM discount_card WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, discountCardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new DiscountCard(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getInt("discount_percentage")
                );
            } else {
                return null;
            }
        }
    }
    public void deleteDiscountCardById(int discountCardId) throws SQLException {
        String query = "DELETE FROM discount_card WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, discountCardId);
            stmt.executeUpdate();
        }
    }
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
