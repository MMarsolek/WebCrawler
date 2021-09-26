package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;


public class UpToDomainSanitizer  implements UrlSanitizer {

    public String sanitizeUrl(String longUrl){
        URL url;
        try {
            url = new URL(longUrl);
        } catch (MalformedURLException e) {
            return longUrl;
        }
        String domain = url.getHost();
        return domain;
    }
}
