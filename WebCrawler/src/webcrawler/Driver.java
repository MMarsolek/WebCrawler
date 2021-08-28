package webcrawler;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Driver{
	 public static void main( String[] args ) throws InterruptedException {
		 Queue<String> urlQueue = new LinkedBlockingQueue<>();
		 Queue<PageData> dataQueue = new LinkedBlockingQueue<>();
		 Counter visitedCounter = new Counter();
		 int numberOfMaxSites;
		 ArrayList<Thread> threads = new ArrayList<>();
		 UrlSanitizer sanitizeUrl = new UpToQuerySanitizer();


		 Scanner sc = new Scanner(System.in);

		 System.out.print("If you would like to set the number of sites visited, please enter the desired number below. " +
				 "Otherwise, please enter \"0\" ");
		 numberOfMaxSites = sc.nextInt();
		 sc.nextLine();


		 if (numberOfMaxSites <= 0) {
			 numberOfMaxSites = 100;
			 System.out.print("The number of sites visited has been set to " + numberOfMaxSites + ".");

		 }
		 System.out.print("If you would like to set the starting site, please enter the complete URL now. Otherwise, please enter either 'N' or \"No\". ");
		 String startUrl = sc.nextLine().toLowerCase();
		 if (startUrl.equals("no") || startUrl.equals("n")) {
			 startUrl = "https://www.google.com/";
		 }
		 System.out.println("The starting site has been set to " + startUrl);

		 urlQueue.add(startUrl);
		 sc.close();
		 final DataParser dp = new DataParser(urlQueue, dataQueue);
		 final DataRequester dr = new DataRequester(urlQueue, dataQueue, visitedCounter,  sanitizeUrl);
			//Add stop function that stops dataparser and requester when maxNum has been reached.

		 for (int i = 0; i <= 100; i++) {
			 Runnable runner;
			 if (i % 2 == 0) {
				 runner = dr;
			 } else {
				 runner = dp;
			 }
			 Thread tr = new Thread(runner);
			 tr.start();
			 threads.add(tr);
		 }
		 //Add to sleep to check counter to see if it should stop dp and dr
		 while(visitedCounter.getCount() < numberOfMaxSites){
			 Thread.sleep(500);
		 }
		 dp.stop();
		 dr.stop();
		 for (Thread t : threads){
			 t.join();
		 }

		 ArrayList<String> visitedList = dr.getMyUrlsVisitedList();


		for (int i = 0; i<numberOfMaxSites; i++) {
			System.out.println((i+ 1) + ". Visited-\t" + visitedList.get(i));
		}
	 }
}
