package webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Queue;

public class DataParser implements Runnable {
    private Queue<PageData> myDataQueue;
    private Queue<String> myUrlQueue;
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

    private boolean getUrlsFromData() throws MalformedURLException {
        PageData pageData = removeUrlFromQueue();
        if (pageData == null) {
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

    private String getDomainFromUrl(String url) throws MalformedURLException {
        URL myUrl = new URL(url);
        String domain = String.format("%s://%s", myUrl.getProtocol(), myUrl.getHost());
        return domain;
    }

    synchronized PageData removeUrlFromQueue() {
        PageData pd = myDataQueue.poll();
        return pd;
    }

    synchronized void addUrlToQueue(String url) {
        myUrlQueue.add(url);
    }

    //This method is called from the driver to stop the parser -- add one to stop the requester
    public void stop() {
        isRunning = false;
    }
}


