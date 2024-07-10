import org.junit.jupiter.api.Test;
import ru.clevertec.check.CSVReader;
import ru.clevertec.check.DiscountCard;
import ru.clevertec.check.Product;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

public class CSVReaderTest {

    @Test
    void testReadProducts() throws IOException {
        String filePath = "src/main/resources/products.csv"; //
        List<Product> products = CSVReader.readProducts(filePath);
        assertFalse(products.isEmpty());
    }

    @Test
    void testReadDiscountCards() throws IOException {
        String filePath = "src/test/resources/discount_cards.csv";
        List<DiscountCard> discountCards = CSVReader.readDiscountCards(filePath);
        assertFalse(discountCards.isEmpty());
    }
}
