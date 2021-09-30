package webcrawler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class DataRequesterTest extends Mockito {
    DataRequester dr;
    HttpClient myClient;
    Queue<String> urlQueue;
    Queue<PageData> dataQueue;
    int minStatusCheckTimes;

    @Before
    public void beforeEachTest(){
        urlQueue = new LinkedBlockingQueue<>();
        dataQueue = new LinkedBlockingQueue<>();
        AtomicInteger counter = new AtomicInteger();
        myClient = Mockito.mock(HttpClient.class);
        minStatusCheckTimes = 5;
        dr = new DataRequester(urlQueue, dataQueue, counter, myClient,minStatusCheckTimes);
    }
    @Test
    public void testGetUrlContents() throws IOException, InterruptedException {
        String fakeUrl = "https://mubar";
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        when(myClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn("Contents of body");
        String testResponse = dr.getURLContents(fakeUrl);
        assertEquals("Contents of body", testResponse);
    }
    @Test
    public void testGetUrlContentsWith400Status() throws IOException, InterruptedException {
        String fakeUrl = "https://mubar";
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        when(myClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);
        assertThrows(IOException.class,()->dr.getURLContents(fakeUrl));
    }

    @Test
    public void testBlankStringToQueueInput() throws InterruptedException {
        urlQueue.add("");
        boolean testResponse =  dr.dataGetter();
        assertFalse(testResponse);
    }

    @Test
    public void testGetUrlContentsWith500ChangedTo200Status() throws IOException, InterruptedException {
        String fakeUrl = "https://mubar";
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        when(myClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("Contents of body");
        String testResponse = dr.getURLContents(fakeUrl);
        assertEquals("Contents of body", testResponse);
    }

    @Test
    public void testGetUrlContentsWith500Status() throws IOException, InterruptedException {
        String fakeUrl = "https://mubar";
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        when(myClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);
        assertThrows(IOException.class,()->dr.getURLContents(fakeUrl));
        Mockito.verify(httpResponse, times(minStatusCheckTimes));
    }
}
