package csci572;

public class FetchedPage {
	String URL;
	Integer statusCode;

	FetchedPage(String url, Integer code) {
		URL = url;
		statusCode = code;
	}

	@Override
	public String toString() {
		return URL + "," + statusCode + "\n";
	}
}
