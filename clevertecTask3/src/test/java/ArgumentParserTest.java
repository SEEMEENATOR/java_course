import org.junit.jupiter.api.Test;
import ru.clevertec.check.ArgumentParser;

import static org.junit.jupiter.api.Assertions.*;

public class ArgumentParserTest {

    @Test
    void testArgumentParsing() {
        String[] args = {
                "datasource.url=jdbc:postgresql://localhost:5432/check",
                "datasource.username=postgres",
                "datasource.password=75980",
                "saveToFile=result.csv",
                "1-2",
                "discountCard=1111",
                "balanceDebitCard=100.0"
        };

        ArgumentParser argumentParser = new ArgumentParser(args);

        assertEquals("jdbc:mysql://localhost:3306/test", argumentParser.getDatasourceUrl());
        assertEquals("root", argumentParser.getDatasourceUsername());
        assertEquals("password", argumentParser.getDatasourcePassword());
        assertEquals("result.csv", argumentParser.getSaveToFile());
        assertEquals(1, argumentParser.getProducts().size());
        assertTrue(argumentParser.getProducts().containsKey(1));
        assertEquals(2, argumentParser.getProducts().get(1));
        assertEquals("1234567890", argumentParser.getDiscountCard());
        assertEquals(100.0, argumentParser.getBalanceDebitCard());
    }

    @Test
    void testMissingArguments() {
        String[] args = {
                "datasource.url=jdbc:mysql://localhost:3306/test",
                "saveToFile=result.csv"
        };

        ArgumentParser argumentParser = new ArgumentParser(args);

        assertNull(argumentParser.getDatasourceUsername());
        assertNull(argumentParser.getDatasourcePassword());
        assertNull(argumentParser.getDiscountCard());
        assertEquals(0, argumentParser.getProducts().size());
        assertEquals("result.csv", argumentParser.getSaveToFile());
    }

    @Test
    void testInvalidArgumentFormat() {
        String[] args = {
                "datasource.url=jdbc:mysql://localhost:3306/test",
                "username=root",
                "password=password",
                "saveToFile=result.csv"
        };

        assertThrows(NumberFormatException.class, () -> new ArgumentParser(args));
    }
}
