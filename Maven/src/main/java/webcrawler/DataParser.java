package webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Queue;

public class DataParser implements Runnable {
    private final Queue<PageData> myDataQueue;
    private final Queue<String> myUrlQueue;
    private boolean isRunning;

    public DataParser(Queue<String> theUrlQueue, Queue<PageData> theDataQueue) {
        myUrlQueue = theUrlQueue;
        myDataQueue = theDataQueue;
        isRunning = false;
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

    public void stop() {
        isRunning = false;
    }
    public boolean checkIfStillRunning(){
        return isRunning;
    }
}


