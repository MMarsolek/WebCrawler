package webcrawler;

class PageData{
	private String myUrl;
	private String myContents;
	
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
	public void setUrl(String thisUrl) {
		myUrl = thisUrl;

	}
	public void setContents(String thisContents) {
		myContents = thisContents;
	}
}