package csci572;

public class WebPage {
	String URL;
	boolean withinDomian;

	public WebPage(String URL, boolean status) {
		this.URL = URL;
		withinDomian = status;
	}

	@Override
	public String toString() {
		return URL + "," + (withinDomian ? "OK" : "N_OK") + "\n";
	}

	@Override
	public boolean equals(Object w) {
		if (w instanceof WebPage) {
			WebPage other = (WebPage) w;
			return URL.equals(other.URL);
		} else {
			return false;
		}
	}
}
