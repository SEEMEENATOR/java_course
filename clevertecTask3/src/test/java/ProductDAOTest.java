import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.check.Product;
import ru.clevertec.check.ProductDAO;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ProductDAOTest {

    private Connection connection;
    private ProductDAO productDAO;

    @BeforeEach
    void setUp() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/check";
        String username = "postgres";
        String password = "75980";
        connection = DriverManager.getConnection(url, username, password);
        productDAO = new ProductDAO(connection);
    }

    @Test
    void testGetAllProducts() throws SQLException {
        List<Product> products = productDAO.getAllProducts();
        assertFalse(products.isEmpty());
    }
}
