package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DataParser {
    private Queue <PageData> myDataQueue;
    private Queue <String> myUrlQueue;

    public DataParser(Queue<String> theUrlQueue, Queue<PageData> theDataQueue){
    	myUrlQueue = theUrlQueue;
    	myDataQueue = theDataQueue;
    }

   
    
    public void getUrlsFromData() {
    	PageData pageData = myDataQueue.poll();
	    String rawPageData = pageData.getContents();
    	Document doc = Jsoup.parse(rawPageData);
    	List<Element> listOfLink = doc.select("a");
    	for(Element link : listOfLink) {
    		String  urlFromData = link.attr("href").toLowerCase();
    			try {
    	    		if(!urlFromData.startsWith("http") && !urlFromData.startsWith("http")) {
						urlFromData = getDomainFromUrl(pageData.getUrl())+(urlFromData);
			    		addUrlToQueue(urlFromData);
			    	
    	    		}else {
			    		addUrlToQueue(urlFromData);
    	    		}
				} catch (MalformedURLException e) {
					System.out.println("Malformed or broken URL" + urlFromData + "\n\t\t" + e.getMessage());
	    		}
	    	}
	    }
    
    
    private String getDomainFromUrl(String url) throws  MalformedURLException {
    	URL myUrl = new URL(url);
    	String domain = String.format("%s://%s", myUrl.getProtocol(), myUrl.getHost());
    	return domain;
    }
    
    
    private void addUrlToQueue(String url){
    	myUrlQueue.add(url);
    }
} 


