import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.clevertec.check.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckRunnerTest {

    @TempDir
    Path tempDir;

    private ArgumentParser argumentParser;
    private ProductDAO productDAO;
    private DiscountCardDAO discountCardDAO;
    private CheckRunner checkRunner;
    private CheckGenerator checkGenerator;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/check";
        String username = "postgres";
        String password = "75980";
        connection = DriverManager.getConnection(url, username, password);

        productDAO = new ProductDAO(connection);
        discountCardDAO = new DiscountCardDAO(connection);

        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        connection.createStatement().execute("CREATE TABLE product (id INT PRIMARY KEY, description VARCHAR(255), price DOUBLE, quantity_in_stock INT, wholesale BOOLEAN)");
        connection.createStatement().execute("CREATE TABLE discount_card (id INT PRIMARY KEY, number VARCHAR(255), discount_percentage INT)");

        connection.createStatement().execute("INSERT INTO product (id, description, price, quantity_in_stock, wholesale) VALUES (1, 'Product 1', 10.0, 100, false)");
        connection.createStatement().execute("INSERT INTO product (id, description, price, quantity_in_stock, wholesale) VALUES (2, 'Product 2', 20.0, 50, true)");
        connection.createStatement().execute("INSERT INTO discount_card (id, number, discount_percentage) VALUES (1, '12345', 10)");
    }

    @Test
    void testMain() throws SQLException, IOException {
        String[] args = {
                "datasource.url=jdbc:postgresql://localhost:5432/check",
                "datasource.username=postgres",
                "datasource.password=75980",
                "saveToFile=" + tempDir.resolve("result.csv").toString(),
                "1-2",
                "discountCard=2222",
                "balanceDebitCard=100.0"
        };

        CheckRunner.main(args);

        Path resultFile = tempDir.resolve("result.csv");
        assertTrue(Files.exists(resultFile));
        List<String> lines = Files.readAllLines(resultFile);
        assertFalse(lines.isEmpty());
    }

    @Test
    void testHandleError() throws IOException {
        String message = "Test error message";
        Path errorFile = tempDir.resolve("error.log");
        CheckRunner.handleError(message, errorFile.toString());
        List<String> lines = Files.readAllLines(errorFile);
        assertEquals(1, lines.size());
        assertEquals(message, lines.get(0));
    }

    @Test
    void testPrintCheckToConsole() {
        Product product = new Product(23, "Milk 2l", 20.0, 100, false);
        DiscountCard discountCard = new DiscountCard(1, "1111", 10);
        Map<Product, Integer> products = Map.of(product, 2);
        Check check = new Check(products, discountCard, 20.0, 2.0, 18.0);
        CheckRunner.printCheckToConsole(check);
    }
}
