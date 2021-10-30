package webcrawler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class WebCrawler {
    private final int myNumberOfMaxSites;
    private final int myNumOfThreads;
    private final int myNumOfDrThreads;
    private final int myNumOfDpThreads;
    private final DataRequester myDataRequestor;
    private final DataParser myDataParser;
    private final AtomicInteger myCounter;
    private PropertyChangeListener myRequesterResultListener;
    private PropertyChangeListener myParserResultListener;
    private HashMap<String, Integer> urlWordCountMap;
    private final  ArrayList<Thread> myThreads;
    private final PropertyChangeSupport propChangeSupport;

    public WebCrawler(int numberOfMaxSites, String startUrl, int numOfThreads){
        this(numberOfMaxSites, startUrl,numOfThreads, numOfThreads, "");
    }

    public WebCrawler(int numberOfMaxSites, String startUrl, int numOfDRThreads, int numOfDPThreads, String wordToSearchFor){
        Queue<String> urlQueue = new LinkedBlockingQueue<>();
        Queue<PageData> dataQueue = new LinkedBlockingQueue<>();
        myNumberOfMaxSites = numberOfMaxSites;
        urlQueue.add(startUrl);
        myCounter = new AtomicInteger();
        myDataParser = new DataParser(urlQueue, dataQueue, wordToSearchFor);
        myDataRequestor= new DataRequester(urlQueue, dataQueue, myCounter);
        myThreads = new ArrayList<>();
        propChangeSupport =  new PropertyChangeSupport(this);
        propertyChangeEvent();
        myDataParser.addPropertyChangeListener(myParserResultListener);
        myDataRequestor.addPropertyChangeListener(myRequesterResultListener);
        myNumOfDpThreads = numOfDPThreads;
        myNumOfDrThreads = numOfDRThreads;
        myNumOfThreads = numOfDPThreads + numOfDRThreads;
        urlWordCountMap = new HashMap<>();

    }

    public void start() throws InterruptedException {
        System.out.println("webcrawler.WebCrawler Started");
        if (myNumOfThreads < 0) {
            for (int i = 0; i < myNumOfThreads; i++) {
                Runnable runner;
                if (i % 3 == 0) {
                    runner = myDataParser;
                } else {
                    runner = myDataRequestor;
                }
                Thread tr = new Thread(runner);
                tr.start();
                myThreads.add(tr);
            }
        } else {
            for (int i = 0; i < myNumOfDpThreads; i++) {
                    Runnable runner;
                    runner = myDataParser;
                    Thread tr = new Thread(runner);
                    tr.start();
                    myThreads.add(tr);
                }
            for (int i = 0; i < myNumOfDrThreads; i++){
                    Runnable runner;
                    runner = myDataRequestor;
                    Thread tr = new Thread(runner);
                    tr.start();
                    myThreads.add(tr);
                }

            }
        }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    public void waitUntilFinished() throws InterruptedException {
        while (myCounter.get() < myNumberOfMaxSites) {
            Thread.sleep(100);
        }
    }

    public boolean isRunning(){
        return myDataRequestor.isRunning();
    }

    public void stop() throws InterruptedException {
        myDataParser.stop();
        myDataRequestor.stop();
        for (Thread t : myThreads){
            t.join();
        }
    }

    public void resetMyCounter(){
        myCounter.set(0);
    }

    private void updateUrlWordCountMap(String url, int wordCount){
        if (url!=null) {
            urlWordCountMap.put(url, wordCount);
        }
    }

    public void propertyChangeEvent(){
        myRequesterResultListener = event -> {
//                propChangeSupport.firePropertyChange(event.getPropertyName(), -1, (int)urlWordCountMap.get(event.getPropertyName()));
                    updateUrlWordCountMap(event.getPropertyName(), -1);
        };
        myParserResultListener = event -> {
            updateUrlWordCountMap(event.getPropertyName(), (int) event.getNewValue());
            //propChangeSupport.firePropertyChange(event.getPropertyName(), 0 ,(int)urlWordCountMap.get(event.getPropertyName()));

        };
          for(String urlString : urlWordCountMap.keySet())
            {
            int wordCountValue = urlWordCountMap.get(urlString);
            if (myCounter.get() < myNumberOfMaxSites) {
                propChangeSupport.firePropertyChange(urlString, -1, wordCountValue);
            }
        }
    }

    //AddToHashMap(Roppd, url)

    public ArrayList<PageData> visitedUrlsList(){
        return myDataRequestor.getMyUrlsVisitedList();
    }

    public void setUrlSanitizer(UrlSanitizer sanitizer){
        myDataRequestor.setMySanitizer(sanitizer);
    }

    public DataRequester getDataRequester(){
        return myDataRequestor;
    }
}

