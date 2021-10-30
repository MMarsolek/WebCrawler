package webcrawler;

public class PageData{
	private String myUrl;
	private String myContents;
	private String mySanitizedUrl;
	
	public PageData(String contents, String url){
		setUrl(url);
		setContents(contents);
	}
	public String getUrl() {
		return myUrl;
	}
	public String getContents() {
		return myContents;
	}
	public String getMySanitizedUrl(){return mySanitizedUrl;}
	public void setMySanitizedUrl(String url){mySanitizedUrl = url;	}
	public void setUrl(String thisUrl) {
		myUrl = thisUrl;
	}
	public void setContents(String thisContents) {
		myContents = thisContents;
	}
}