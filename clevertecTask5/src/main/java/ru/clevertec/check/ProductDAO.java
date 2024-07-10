package ru.clevertec.check;

import ru.clevertec.check.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT id, description, price, quantity_in_stock, wholesale FROM product";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getBoolean("wholesale")
                );
                products.add(product);
            }
        }
        return products;
    }
}
