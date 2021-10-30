package webcrawler;

import org.junit.Before;
import org.junit.Test;
import java.net.MalformedURLException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import static org.junit.Assert.*;

public class DataParserTest {

    DataParser dataParser;
    Queue<String> urlQueue;
    Queue<PageData> dataQueue;

    @Before
    public void beforeEachTest(){
        urlQueue = new LinkedBlockingQueue<>();
        dataQueue = new LinkedBlockingQueue<>();
        dataParser  = new DataParser(urlQueue, dataQueue);
    }

    @Test
    public void getDomainFromUrlTest() throws MalformedURLException {
        String testUrl = "https://www.testUrl.com/thisPartShoulbeRemoved/ad/wkm";
        String results = dataParser.getDomainFromUrl(testUrl);
        assertEquals("https://www.testUrl.com", results);
    }
    @Test
    public void addUrlToQueueTest(){
        dataParser.addUrlToQueue("http://www.testUrl.com");
        assertEquals("http://www.testUrl.com", urlQueue.peek());
    }
    @Test
    public void addNullUrlToQueueTest(){
        dataParser.addUrlToQueue(null);
        assertNull(urlQueue.peek());
    }
    @Test
    public void addBlankUrlToQueueTest(){
        dataParser.addUrlToQueue("");
        assertNull(urlQueue.peek());
    }

    @Test
    public void getUrlsFromDataBasicUrlExtractionTest() throws MalformedURLException {
        String html = "<a class=\"gb_f\" data-pid=\"23\" href=\"https://mail.google.com/mail/?tab=rm&amp;ogbl\" target=\"_top\">Gmail</a>";
        PageData pageData = new PageData(html, "https://www.testUrl.com");
        dataQueue.add(pageData);
        boolean results = dataParser.getUrlsFromData();
        assertTrue(results);
    }

    @Test
    public void getUrlsFromDataNoTagAndSchemeExtractionTest() throws MalformedURLException {
        String html = "<a class=\"gb_f\" data-pid=\"23\"\"mail.google.com/mail/?tab=rm&amp;ogbl\" target=\"_top\">Gmail</a>";
        PageData pageData = new PageData(html, "https://www.testUrl.com");
        dataQueue.add(pageData);
        boolean results = dataParser.getUrlsFromData();
        assertTrue(results);
    }

    @Test
    public void getUrlsFromDataReturnsFalseOnBlankDataTest() throws MalformedURLException {
        String html = "  ";
        PageData pageData = new PageData(html, "https://www.testUrl.com");
        dataQueue.add(pageData);
        boolean results = dataParser.getUrlsFromData();
        assertFalse(results);
    }

    @Test
    public void getNullUrlsFromDataTest() throws MalformedURLException {
        PageData pageData = new PageData(null, "https://www.testUrl.com");
        dataQueue.add(pageData);
        boolean results = dataParser.getUrlsFromData();
        assertFalse(results);
    }

    @Test
    public void numberOfTimesWordsAppearOnlyCorrectTest(){
        int testResults = dataParser.numberOfTimesWordAppears("This this sthis ssthiss ThIs", "this");
        assertEquals(5, testResults);
    }

    @Test
    public void numberOfTimesWordsAppearMixInputTest(){
        int testResults = dataParser.numberOfTimesWordAppears("Today is a great day! This will be a memorable day.", "this");
        assertEquals(1, testResults);

    }
    @Test
    public void numberOfTimesWordsAppearNoCorrectTest(){
        int testResults = dataParser.numberOfTimesWordAppears("Today is a great day!", "this");
        assertEquals(0, testResults);
    }

    @Test
    public void numberOfTimesWordsAppearNullWordTest(){
        assertThrows(NullPointerException.class, ()->{
            dataParser.numberOfTimesWordAppears("Today is a great day!", null);
        });
    }
    @Test
    public void numberOfTimesWordsAppearNullDataTest(){
        int testResults = dataParser.numberOfTimesWordAppears(null, "this");
        assertEquals(0, testResults);
    }

    @Test
    public void numberOfTimesWordsAppearRunOnSentenceTest(){
        int testResults = dataParser.numberOfTimesWordAppears("Thisisarunonsentence. Ihopeitcanfindthecorrectnumberinthis.", "this");
        assertEquals(2, testResults);
    }
    @Test
    public void numberOfTimesWordsAppearEndWithPeriodTest(){
        int testResults = dataParser.numberOfTimesWordAppears("This is the end.", "end");
        assertEquals(1, testResults);
    }

    @Test
    public void numberOfTimesWordsAppearContainTabTest(){
        int testResults = dataParser.numberOfTimesWordAppears("This is   the end.   end     ", "end");
        assertEquals(2, testResults);
    }


}
