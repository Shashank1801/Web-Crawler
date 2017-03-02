package csci572;

public class ProcessedPage {
	String URL;
	Integer outLinks;
	String contentType;
	Integer size;
	String type;

	ProcessedPage(String url, Integer links, String type, Integer sz) {
		URL = url;
		outLinks = links;
		contentType = type;
		size = sz;
		int index = contentType.lastIndexOf(";");
		if (index == -1) {
			this.type = contentType;
		} else {
			this.type = contentType.substring(0, index);
		}
	}

	@Override
	public String toString() {
		return URL + "," + size + "," + outLinks + "," + type + "\n";
	}
}
