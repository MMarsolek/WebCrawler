package webcrawler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler {
    private final int myNumberOfMaxSites;
    private final int myNumOfThreads;
    private final DataRequester myDataRequestor;
    private final DataParser myDataParser;
    private AtomicInteger myCounter;
    private PropertyChangeListener myResultListener;
    private final  ArrayList<Thread> myThreads;
    private final PropertyChangeSupport propChangeSupport;

    public WebCrawler(int numberOfMaxSites, String startUrl, int numOfThreads){
        Queue<String> urlQueue = new LinkedBlockingQueue<>();
        Queue<PageData> dataQueue = new LinkedBlockingQueue<>();

        myNumberOfMaxSites = numberOfMaxSites;
        myNumOfThreads = numOfThreads;
        urlQueue.add(startUrl);
        myCounter = new AtomicInteger();
        myDataParser = new DataParser(urlQueue, dataQueue);
        myDataRequestor= new DataRequester(urlQueue, dataQueue, myCounter);
        myThreads = new ArrayList<>();
        propChangeSupport =  new PropertyChangeSupport(this);
        propertyChangeEvent();
        myDataRequestor.addPropertyChangeListener(myResultListener);
    }



    public void start() throws InterruptedException{
        System.out.println("webcrawler.WebCrawler Started");
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


    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

//MyFrame listens to WC listens to DR
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

    public void propertyChangeEvent(){
        myResultListener = event -> {
            if (myCounter.get() < myNumberOfMaxSites) {
                propChangeSupport.firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        };

    }

    public ArrayList<String> visitedUrlsList(){
        return myDataRequestor.getMyUrlsVisitedList();
    }

    public void setUrlSanitizer(UrlSanitizer sanitizer){
        myDataRequestor.setMySanitizer(sanitizer);
    }

    public DataRequester getDataRequester(){
        return myDataRequestor;
    }
}

