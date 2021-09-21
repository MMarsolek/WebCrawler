package webcrawler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompleteUrlSanitizerTest {
    private CompleteUrlSanitizer myTestSanitizer;

    @BeforeEach
    public void setupBeforeEachTest() {
        myTestSanitizer = new CompleteUrlSanitizer();
    }

    @Test
    public void testSanitizerReturnsSameString() {
        final String testString = "https://www.google.com?pee=ess";
        final String result = myTestSanitizer.sanitizeUrl(testString);
        assert result.equals(testString);
    }

    @Test
    public void testSanitizerReturnsDifferentString() {
        final String testString = "https://www.google.com?pee=ess";
        final String result = myTestSanitizer.sanitizeUrl(testString);
        assert !result.equals("https://www.google.com/Whatever");
    }

    @Test
    public void testSanitizerNullInput() {
        Throwable thrown = assertThrows(NullPointerException.class,()-> myTestSanitizer.sanitizeUrl(null));
        assertNull(thrown.getMessage());
    }
    @Test
    public void testSanitizerBlankInput() {
        assertEquals(null, myTestSanitizer.sanitizeUrl(""));
    }


}
