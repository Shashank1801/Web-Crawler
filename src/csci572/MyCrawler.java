package csci572;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	private final static Pattern NOTALLOWED = Pattern.compile(".*(\\.(css|js|xml|json" + "|mp3|mp3|zip|gz))$");
	private final static Pattern ALLOWED = Pattern.compile(".*(\\.(html|pdf|doc|png|jpeg|jpg|bmp))$");
	public static List<ProcessedPage> pPages;
	public static List<FetchedPage> fPages;
	public static List<WebPage> allPages;
	FileOutputStream fetchWriter;
	FileOutputStream processedWriter;
	FileOutputStream urlsWriter;

	public void onStart() {
		pPages = Collections.synchronizedList(new ArrayList<ProcessedPage>());
		fPages = Collections.synchronizedList(new ArrayList<FetchedPage>());
		allPages = Collections.synchronizedList(new ArrayList<WebPage>());
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		boolean status = (ALLOWED.matcher(href).matches() || !NOTALLOWED.matcher(href).matches())
				&& (href.startsWith("http://www.nytimes.com/") || href.startsWith("https://www.nytimes.com/"));
		if (href != null) {
			WebPage w = new WebPage(href, status);
			allPages.add(w);
		}

		return status;
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			ProcessedPage pp = new ProcessedPage(url, links.size(), page.getContentType(), html.length());
			pPages.add(pp);
		} else if (page.getParseData() instanceof BinaryParseData) {
			BinaryParseData binaryData = (BinaryParseData) page.getParseData();
			String html = binaryData.getHtml();
			ProcessedPage pp = new ProcessedPage(url, 0, page.getContentType(), html.length());
			pPages.add(pp);
		} else if (page.getParseData() instanceof TextParseData) {
			TextParseData textData = (TextParseData) page.getParseData();
			String html = textData.getTextContent();
			ProcessedPage pp = new ProcessedPage(url, textData.getOutgoingUrls().size(), page.getContentType(),
					html.length());
			pPages.add(pp);
		} else {
			pPages.add(new ProcessedPage(url, page.getParseData().getOutgoingUrls().size(), page.getContentType(), 12));
		}
	}

	@Override
	public void onBeforeExit() {
		try {
			urlsWriter = new FileOutputStream("urls_NYTimes.csv");
			for (WebPage wp : allPages) {
				urlsWriter.write(wp.toString().getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				urlsWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// write all fetch data
		try {
			fetchWriter = new FileOutputStream("fetch_NYTimes.csv");
			for (FetchedPage fp : fPages) {
				fetchWriter.write(fp.toString().getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fetchWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// write all processed data
		try {
			processedWriter = new FileOutputStream("visit_NYTimes.csv");
			for (ProcessedPage pp : pPages) {
				processedWriter.write(pp.toString().getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				processedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		FetchedPage fp = new FetchedPage(webUrl.toString(), statusCode);
		fPages.add(fp);
	}

}
