package ru.clevertec;

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
        String query = "SELECT * FROM product";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getBoolean("wholesale")
                );
                products.add(product);
            }
        }
        return products;
    }

    public void reduceProductQuantity(int productId, int quantity) throws SQLException {
        String query = "UPDATE product SET quantity_in_stock = quantity_in_stock - ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
    }

    public Product getProductById(int productId) throws SQLException {
        String query = "SELECT * FROM product WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getBoolean("wholesale")
                );
            } else {
                return null;
            }
        }
    }

    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO product (description, price, quantity_in_stock, wholesale) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getDescription());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantityInStock());
            statement.setBoolean(4, product.isWholesale());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getInt(1));
            }
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE product SET description = ?, price = ?, quantity_in_stock = ?, wholesale = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getDescription());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantityInStock());
            statement.setBoolean(4, product.isWholesale());
            statement.setInt(5, product.getId());
            statement.executeUpdate();
        }
    }
    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM product WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.executeUpdate();
        }
    }
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
