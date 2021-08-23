import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Driver extends Thread{
	 public static void main( String[] args )
	    {
	    	Queue<String> urlQueue = new LinkedList<String>();
	    	Queue<PageData> dataQueue = new LinkedList<PageData>();

	    	int pageCount = 0;
	    	Scanner sc = new Scanner(System.in);
	    	System.out.print("If you would like to set the number of sites visited, please enter the desired number below. Otherwise, please enter \"0\" ");
	    	int numberOfMaxSites = sc.nextInt();
	    	sc.nextLine();
	    	
	    	if (numberOfMaxSites <= 0) {
	    		numberOfMaxSites = 100;
	    		System.out.print("The number of sites visited has been set to " + numberOfMaxSites+".");

	    	}
	    	System.out.print("If you would like to set the starting site, please enter the complete URL now. Otherwise, please enter either 'N' or \"No\". ");	    	
	    	String startUrl = sc.nextLine().toLowerCase();
	    	if (startUrl.equals("no") || startUrl.equals("n")) {
		    	startUrl = "https://www.google.com/";
	    	}
    		System.out.println("The starting site has been set to " + startUrl);

	    	urlQueue.add(startUrl);
	    	DataParser dp = new DataParser(urlQueue, dataQueue);
	    	DataRequester dr = new DataRequester(urlQueue, dataQueue);

	    	while(pageCount <= numberOfMaxSites) {
	    		if(urlQueue.isEmpty()) {
		    		System.out.println("The program has run out of URLs.");

			    	System.out.println("What URL would you like to visit next?");	
			    	String newUrl = sc.nextLine();
			    	if (newUrl.equals("") || newUrl.equals(null)) {
				    	numberOfMaxSites = pageCount;
			    	}else{
			    		urlQueue.add(newUrl);
			    		}
			    	}
	    		if(!urlQueue.isEmpty()) {
			    	dr.dataGetter();
			    	dp.getUrlsFromData();
	    		}
		    	pageCount++;
	    	}
	    	sc.close();
	    	System.out.println("Complete!");
	    	
	    }

}
