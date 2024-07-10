import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.check.*;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class CheckGeneratorTest {

    private List<Product> products;
    private List<DiscountCard> discountCards;

    @BeforeEach
    void setUp() {

        products = Arrays.asList(
                new Product(23, "Oil 2l", 10.0, 100, false),
                new Product(24, "Chocolate", 5.0, 50, true)
        );

        discountCards = Collections.singletonList(new DiscountCard(1, "2222", 10));
    }

    @Test
    void testGenerateCheckWithValidInputs() throws IOException {
        CheckGenerator checkGenerator = new CheckGenerator(products, discountCards);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(23, 24);

        String discountCardNumber = "2222";
        double balanceDebitCard = 100.0;

        Check check = checkGenerator.generateCheck(productQuantities, discountCardNumber, balanceDebitCard);

        assertNotNull(check);
        assertEquals(1, check.getDiscountCard().getId());
        assertEquals(20.0, check.getTotalPrice());
        assertEquals(2.0, check.getTotalDiscount());
        assertEquals(18.0, check.getTotalWithDiscount());
    }

    @Test
    void testGenerateCheckWithInsufficientBalance() throws IOException {
        CheckGenerator checkGenerator = new CheckGenerator(products, discountCards);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(23, 24);

        String discountCardNumber = "2222";
        double balanceDebitCard = 10.0; //

        assertThrows(IllegalArgumentException.class, () ->
                checkGenerator.generateCheck(productQuantities, discountCardNumber, balanceDebitCard));
    }
}
