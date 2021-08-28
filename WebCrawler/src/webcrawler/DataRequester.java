package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;


public class DataRequester implements Runnable {

	private Queue<String> myUrlQueue;
	private Queue<PageData> myDataQueue;
	private ArrayList<String> myUrlsVisitedList;
	private HashSet<String> myUrlsVisitedSet;
	private Counter pageCount;
	private boolean isRunning;
	private UrlSanitizer mySanitizer;


	public DataRequester(Queue<String> theUrlQueue, Queue<PageData> theDataQueue, Counter counter,  UrlSanitizer sanitizeUrls) {
		myUrlQueue = theUrlQueue;
		myDataQueue = theDataQueue;
		myUrlsVisitedSet = new HashSet<>();
		pageCount = counter;
		myUrlsVisitedList = new ArrayList<>();
		isRunning = false;
		mySanitizer = sanitizeUrls;
	}

	public void run() {
		isRunning =true;
		while(isRunning) {
			try {
				if (dataGetter()) {
					pageCount.increment();
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
		String sanitizedUrl = mySanitizer.sanitizeUrl(url);
		if(url == null || isInVisitedSet(sanitizedUrl)) {
				return false;
		}
			try {
				contents = getURLContents(url);
				PageData pd = new PageData(contents, url);
				addUrlToSet(sanitizedUrl);
				addToQueue(pd,url);
				return true;

			} catch (IOException e) {
				//System.out.println("Website not found. " + url + "\t\t - " + e.getMessage());
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
		// Look up try with Resources
		in.close();
		return output.toString();
	}

	synchronized void addUrlToSet(String url){
		myUrlsVisitedSet.add(url);
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
		myUrlsVisitedList.add(url);
		myDataQueue.add(pd);
	}

	public void stop(){
		isRunning = false;
	}
}
//Figure out how to stop both requestors. Maybe a sigInt? Something that will call the stop() at a certain point.
//BreathFirst vs DepthFirst for links. Add boolean to different methods allowing you to choose how to search.
