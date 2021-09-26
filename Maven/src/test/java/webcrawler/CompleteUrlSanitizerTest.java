package webcrawler;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;


public class CompleteUrlSanitizerTest {
    private CompleteUrlSanitizer myTestSanitizer;

    @Before
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
        assertThrows(NullPointerException.class,()-> myTestSanitizer.sanitizeUrl(null));
    }
    @Test
    public void testSanitizerBlankInput() {
        assertEquals(null, myTestSanitizer.sanitizeUrl(""));
    }


}
