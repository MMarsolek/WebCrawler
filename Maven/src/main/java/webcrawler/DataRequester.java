package webcrawler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;


public class DataRequester implements Runnable {

    private final Queue<String> myUrlQueue;
    private final Queue<PageData> myDataQueue;
    private final ArrayList<PageData> myUrlsVisitedList;
    private final HashSet<String> myUrlsVisitedSet;
    private final AtomicInteger pageCount;
    private boolean isRunning;
    private UrlSanitizer mySanitizer;
    private final HttpClient myClient;
    private final int statusRetryCounter;


    private final PropertyChangeSupport propChangeSupport;

    public DataRequester(Queue<String> theUrlQueue, Queue<PageData> theDataQueue, AtomicInteger counter) {
        this(theUrlQueue, theDataQueue, counter, HttpClient.newHttpClient(), 5);

    }

    public DataRequester(Queue<String> theUrlQueue, Queue<PageData> theDataQueue, AtomicInteger counter, HttpClient client, int retryCounter) {
        myUrlQueue = theUrlQueue;
        myDataQueue = theDataQueue;
        myUrlsVisitedSet = new HashSet<>();
        pageCount = counter;
        myUrlsVisitedList = new ArrayList<>();
        isRunning = false;
        myClient = client;
        propChangeSupport = new PropertyChangeSupport(this);
        statusRetryCounter = retryCounter;
    }

    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                if (dataGetter()) {
                    pageCount.getAndAdd(1);
                } else {
                    Thread.sleep(500);
                }
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            } catch (BadUrlException badUrl) {
                System.out.println(badUrl.getMessage() + badUrl.getBadUrl());
            }
        }
    }

    // Add "Final" to all final objects and variables
    boolean dataGetter() throws InterruptedException {
        String url = removeFromQueue();
        String contents;
        if (url != null && !url.isBlank()) {
            String sanitizedUrl = mySanitizer.sanitizeUrl(url);
            if (isInVisitedSet(sanitizedUrl)) {
                return false;
            }
            try {
                contents = getURLContents(url);
                PageData pd = new PageData(contents, url);
                pd.setMySanitizedUrl(sanitizedUrl);
                addUrlToSet(pd);
                addToQueue(pd);
                return true;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Takes the URLString and turns it into an URL object. It then opens the connection to the site belonging to the URL
     * The Try Catch will print any errors that appear during this process.
     *
     * @param urlString
     * @return String
     * @throws IOException
     */
    String getURLContents(String urlString) throws IOException, InterruptedException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        } catch (final IllegalArgumentException ex) {
            throw new BadUrlException(urlString, ex);
        }
        HttpResponse<String> response = myClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        int statusTryCounter = 1;
        if (statusCode < 400) {
            return response.body();
        }
        do {
            Thread.sleep(500);
            response = myClient.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            statusTryCounter++;
        } while (statusCode > 499 && statusTryCounter <= statusRetryCounter);
        throw new IOException("Status Code: " + statusCode);
    }

    public void setMySanitizer(UrlSanitizer sanitizer) {
        mySanitizer = sanitizer;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    synchronized void addUrlToSet(PageData pd) {
        myUrlsVisitedSet.add(pd.getMySanitizedUrl());
        myUrlsVisitedList.add(pd);

    }

    synchronized boolean isInVisitedSet(String url) {
        return myUrlsVisitedSet.contains(url);
    }

    synchronized String removeFromQueue() {
        return myUrlQueue.poll();
    }

    public ArrayList<PageData> getMyUrlsVisitedList() {
        return myUrlsVisitedList;
    }

    synchronized void addToQueue(PageData pd) {
        myDataQueue.add(pd);
        propertyChangeEvent();
    }

    public void propertyChangeEvent() {
        PageData pd = myUrlsVisitedList.get(myUrlsVisitedList.size() - 1);
        String url = pd.getMySanitizedUrl();
        propChangeSupport.firePropertyChange(url, myUrlsVisitedList.size() - 1, myUrlsVisitedList.size());
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
