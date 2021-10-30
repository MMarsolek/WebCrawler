package webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser implements Runnable{
    private final Queue<PageData> myDataQueue;
    private final Queue<String> myUrlQueue;
    private boolean isRunning;
    private String myWordToSearchFor;
    private final PropertyChangeSupport propChangeSupport;


    public DataParser(Queue<String> theUrlQueue, Queue<PageData> theDataQueue) {
        this(theUrlQueue, theDataQueue, null);
    }
    public DataParser(Queue<String> theUrlQueue, Queue<PageData> theDataQueue, String wordToSearchFor) {
        myUrlQueue = theUrlQueue;
        myDataQueue = theDataQueue;
        isRunning = false;
        myWordToSearchFor = wordToSearchFor;
        propChangeSupport = new PropertyChangeSupport(this);
    }


    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                if (!getUrlsFromData()) {
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean getUrlsFromData() throws MalformedURLException {
        PageData pageData = removeUrlFromQueue();
        if (pageData == null || pageData.getContents() == null || pageData.getContents().isBlank()) {
            return false;
        }
        String rawPageData = pageData.getContents();
        Document doc = Jsoup.parse(rawPageData);
        getUrlFromDoc(doc, pageData);
        int countOfWord = numberOfTimesWordAppears(pageData.getContents(), myWordToSearchFor);
        propertyChangeEvent(pageData,countOfWord);

        return true;
    }

    private void getUrlFromDoc(Document doc, PageData pageData) throws MalformedURLException {
        List<Element> listOfLink = doc.select("a");
        for (Element link : listOfLink) {
            String urlFromData = link.attr("href").toLowerCase();
            if (!urlFromData.contains("://")) {
                urlFromData = getDomainFromUrl(pageData.getUrl()) + (urlFromData);
            }
            addUrlToQueue(urlFromData);
        }
    }

    String getDomainFromUrl(String url) throws MalformedURLException {
        URL myUrl = new URL(url);
        return String.format("%s://%s", myUrl.getProtocol(), myUrl.getHost());
    }

    synchronized PageData removeUrlFromQueue() {
        return myDataQueue.poll();
    }

    synchronized void addUrlToQueue(String url) {
        if(url!=null && !url.isBlank()){
            myUrlQueue.add(url);
        }
    }

    public int numberOfTimesWordAppears(String rawData, String wordToFind){
        int count = 0;
        if (rawData == null || rawData.isBlank() || wordToFind.isBlank()) {
            return count;
        }
        rawData = rawData.toLowerCase();
        wordToFind = wordToFind.toLowerCase();
        Pattern pattern  = Pattern.compile(wordToFind);
        Matcher m = pattern.matcher(rawData);
        count = (int) m.results().count();
        return count;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    public void propertyChangeEvent(PageData pd, int countOfWords) {
        String url = pd.getMySanitizedUrl();
        propChangeSupport.firePropertyChange(url, -1, countOfWords);
    }


    public void stop() {
        isRunning = false;
    }
    public boolean checkIfStillRunning(){
        return isRunning;
    }
}



