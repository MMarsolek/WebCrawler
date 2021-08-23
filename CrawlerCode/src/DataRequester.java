
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;

public class DataRequester {

    private Queue<String> myUrlQueue;
    private Queue<PageData> myDataQueue;
    private ArrayList<String> myUrlsVisitedList;    
    private HashSet<String> myUrlsVisitedSet;


    
    public DataRequester(Queue<String> theUrlQueue, Queue<PageData> theDataQueue){
    	myUrlQueue = theUrlQueue;
    	myDataQueue = theDataQueue;
    	myUrlsVisitedList  = new ArrayList<String>();
    }
    public void dataGetter() {
    	
    		String url = myUrlQueue.remove();
    		String contents;
//    		while(myUrlsVisitedSet.contains(url)) {
//    			url = myUrlQueue.remove();
//    		}
			try {
				contents = getURLContents(url);
	    		PageData pd = new PageData(contents,url);
	    		myDataQueue.add(pd);

	    		myUrlsVisitedList.add(url);
	    		//myUrlsVisitedSet.add(url);
				System.out.println("Visited -\t"+url);
				

			} catch (IOException e) {
				System.out.println("Website not found. " + url + "\t\t - " + e.getMessage());
				e.printStackTrace();
			}

    }
    
   
    private String getURLContents(String urlString) throws IOException{

        //Takes the URLString and turns it into an URL object. It then opens the connection to the site belonging to the URL
        //The Try Catch will print any errors that appear during this process.
        String inputLine;
        StringBuilder output = new StringBuilder();

        	URL theURL = new URL(urlString);
            BufferedReader in = new BufferedReader(new InputStreamReader(theURL.openStream()));
            

            while ((inputLine = in.readLine()) != null ) {
            	output.append(inputLine);
            }

            in.close();
        return output.toString();
    }   
    
    public ArrayList<String> getVisitedUrls(){
    	return myUrlsVisitedList;
    }
}

//HashSet to ensure no dups
//BreathFirst vs DepthFirst for links. Add boolean to different methods allowing you to choose how to search.
//Move ArrayList to this class for URL storage
// Make tag on GitHub