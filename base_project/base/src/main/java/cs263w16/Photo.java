package cs263w16;

public class Photo {
	private User author;
	private String URL;
	private String label;
	
	public Photo(User author, String URL)
	{
		this.author=author;
		this.URL=URL;
	}
	
	public String getURL() {
		return URL;
	}
	
	public void setURL(String uRL) {
		URL = uRL;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}
