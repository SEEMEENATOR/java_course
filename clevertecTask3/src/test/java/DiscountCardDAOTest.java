import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.check.DiscountCard;
import ru.clevertec.check.DiscountCardDAO;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DiscountCardDAOTest {

    private Connection connection;
    private DiscountCardDAO discountCardDAO;

    @BeforeEach
    void setUp() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/check";
        String username = "postgres";
        String password = "75980";
        connection = DriverManager.getConnection(url, username, password);
        discountCardDAO = new DiscountCardDAO(connection);
    }

    @Test
    void testGetAllDiscountCards() throws SQLException {
        List<DiscountCard> discountCards = discountCardDAO.getAllDiscountCards();
        assertFalse(discountCards.isEmpty());
    }
}
