package webcrawler;

public class BadUrlException extends RuntimeException{
    private final String myBadUrl;
    public BadUrlException(final String badUrl, Exception innerEx) {
        super(innerEx);
        myBadUrl = badUrl;
    }

    public String getBadUrl() {
        return myBadUrl;
    }
}
