package webcrawler;

public class UpToQuerySanitizer implements UrlSanitizer {

    public String sanitizeUrl(String longUrl){
        if( longUrl != null && longUrl.contains("?")) {
            int firstQuestionMark = longUrl.indexOf('?') - 1;
            longUrl = longUrl.substring(0, (firstQuestionMark));
        }
        return longUrl;
    }
}
