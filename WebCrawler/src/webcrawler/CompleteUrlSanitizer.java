package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;

public class CompleteUrlSanitizer   implements UrlSanitizer {

    public String sanitizeUrl(String longUrl){

        if(longUrl.equals(null) || longUrl.isBlank()){
            return null;
        }
        URL url;
        try {
                url = new URL(longUrl);

        } catch (MalformedURLException e) {
            return longUrl;
        }
        String completeUrl = url.toString();
        return completeUrl;
    }

}
