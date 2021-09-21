package webcrawler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;


public class DataRequester implements Runnable{

	private final Queue<String> myUrlQueue;
	private final Queue<PageData> myDataQueue;
	private final ArrayList<String> myUrlsVisitedList;
	private final HashSet<String> myUrlsVisitedSet;
	private AtomicInteger pageCount;
	private PropertyChangeListener myResultListener;
	private boolean isRunning;
	private UrlSanitizer mySanitizer;

	private final PropertyChangeSupport propChangeSupport;

	public DataRequester(Queue<String> theUrlQueue, Queue<PageData> theDataQueue, AtomicInteger counter) {
		myUrlQueue = theUrlQueue;
		myDataQueue = theDataQueue;
		myUrlsVisitedSet = new HashSet<>();
		pageCount = counter;
		myUrlsVisitedList = new ArrayList<>();
		isRunning = false;
		propChangeSupport = new PropertyChangeSupport(this);
	}

	public void run() {
		isRunning =true;
		while(isRunning) {
			try {
				if (dataGetter()) {
					pageCount.getAndAdd(1);
				}else{
					Thread.sleep(500);
				}
				} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
// Add "Final" to all final objects and variables
	private boolean dataGetter() throws InterruptedException {
		String url = removeFromQueue();
		String contents;
		if(url!=null) {
			String sanitizedUrl = mySanitizer.sanitizeUrl(url);
			if (isInVisitedSet(sanitizedUrl)) {
				return false;
			}
			try {
				contents = getURLContents(url);
				PageData pd = new PageData(contents, url);
				addUrlToSet(sanitizedUrl);
				addToQueue(pd, url);
				return true;
			} catch (IOException e) {
			}
		}
			return false;
	}

	/**
	 * 	Takes the URLString and turns it into an URL object. It then opens the connection to the site belonging to the URL
	 * 	The Try Catch will print any errors that appear during this process.
	 * @param urlString
	 * @return String
	 * @throws IOException
	 */
	private String getURLContents(String urlString) throws IOException {
		String inputLine;
		StringBuilder output = new StringBuilder();
		URL theURL = new URL(urlString);
		BufferedReader in = new BufferedReader(new InputStreamReader(theURL.openStream()));
		while ((inputLine = in.readLine()) != null) {
			output.append(inputLine);
		}
		in.close();
		return output.toString();
	}

	public void setMySanitizer(UrlSanitizer sanitizer){
		mySanitizer = sanitizer;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupport.addPropertyChangeListener(listener);
	}


	synchronized void addUrlToSet(String url){
		myUrlsVisitedSet.add(url);
		myUrlsVisitedList.add(url);

	}

	synchronized boolean isInVisitedSet(String url){
		return myUrlsVisitedSet.contains(url);
	}

	synchronized String removeFromQueue(){
		return myUrlQueue.poll();
	}

	public ArrayList<String> getMyUrlsVisitedList(){
		return myUrlsVisitedList;
	}
//Shortens URLS for sanitization (Shortens at the ?)
//Look into the Thread locks to synchronize the HashSet and the queues

	synchronized void addToQueue(PageData pd, String url) {
		myDataQueue.add(pd);
		propertyChangeEvent();
	}

	public void propertyChangeEvent(){
		String url = myUrlsVisitedList.get(myUrlsVisitedList.size()-1);
		propChangeSupport.firePropertyChange(url, myUrlsVisitedList.size()-1, myUrlsVisitedList.size());
	}

	public void stop(){
		isRunning = false;
	}

	public boolean isRunning(){
		return isRunning;
	}
}

//Figure out how to stop both requestors. Maybe a sigInt? Something that will call the stop() at a certain point.
//BreathFirst vs DepthFirst for links. Add boolean to different methods allowing you to choose how to search.
