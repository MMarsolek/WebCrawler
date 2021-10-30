package webcrawler.cmdline;

import webcrawler.*;

import java.util.Scanner;

public class Driver{
	 public static void main( String[] args ) throws InterruptedException {

		 int numberOfMaxSites;
		 int numOfThreads;
		 String sanitizer;

		 Scanner sc = new Scanner(System.in);

		 System.out.print("If you would like to set the number of sites visited, please enter the desired number below. " +
				 "Otherwise, please enter \"0\"  \n");
		 numberOfMaxSites = sc.nextInt();
		 sc.nextLine();


		 if (numberOfMaxSites <= 0) {
			 numberOfMaxSites = 100;
			 System.out.print("The number of sites visited has been set to " + numberOfMaxSites + ".");

		 }

		 System.out.print("If you would like to set the number of threads used, please enter the desired number below. " +
				 "Otherwise, please enter \"0\" \n");
		 numOfThreads = sc.nextInt();
		 sc.nextLine();


		 if (numOfThreads <= 0) {
			 numOfThreads = 100;
			 System.out.print("The number of threads used has been set to " + numOfThreads + ".");

		 }

		 System.out.print("If you would like to set the starting site, please enter the complete URL now. Otherwise, please enter either 'N' or \"No\". \n");
		 String startUrl = sc.nextLine().toLowerCase();
		 if (startUrl.equals("no") || startUrl.equals("n")) {
			 startUrl = "https://www.google.com/";
		 }

		 System.out.println("The starting site has been set to " + startUrl);
		 WebCrawler wc = new WebCrawler(numberOfMaxSites, startUrl, numOfThreads);
		// sc.nextLine();

		 System.out.print("\nIf you would like to filter the URLs please choose either \"Domain\", \"Query\", or \"Complete\".");
		 sanitizer = sc.nextLine().toLowerCase();
		 if(sanitizer.equals("domain")){
			 wc.setUrlSanitizer(new UpToDomainSanitizer());
			 System.out.println("Sanitizer set to domain ");

		 } else if(sanitizer.equals("query")){
			 wc.setUrlSanitizer(new UpToQuerySanitizer());
			 System.out.println("Sanitizer set to query");

		 }else {
			 wc.setUrlSanitizer(new CompleteUrlSanitizer());
			 System.out.println("Sanitizer set to complete");

		 }

		 wc.start();
		 sc.close();

		 wc.waitUntilFinished();
		 wc.stop();
		 for(PageData url : wc.visitedUrlsList()){
			 System.out.println(url.getMySanitizedUrl());
		 }
	 }
}
