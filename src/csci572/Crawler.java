package csci572;

import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeMap;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Crawler {
	public static void main(String[] args) throws Exception {
		String crawlStorageFolder = "/data/crawl";
		int numberOfCrawlers = 10;
		int maxDepth = 16;
		int maxPageToFetch = 20;

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxDepthOfCrawling(maxDepth);
		config.setMaxPagesToFetch(maxPageToFetch);
		config.setIncludeHttpsPages(true);
		config.setIncludeBinaryContentInCrawling(true);

		/*
		 * Instantiate the controller for this crawl.
		 */

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		Date start = new Date();
		System.out.println(new Date());
		controller.addSeed("http://www.nytimes.com/");

		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(MyCrawler.class, numberOfCrawlers);

		System.out.println("===========STATS==================");
		TreeMap<Integer, Integer> urlmap = new TreeMap<>();
		for (FetchedPage f : MyCrawler.fPages) {
			if (urlmap.get(f.statusCode) == null) {
				urlmap.put(f.statusCode, 1);
			} else {
				urlmap.put(f.statusCode, urlmap.get(f.statusCode) + 1);
			}
		}
		int succeeded = 0;
		int moved = 0;
		int failed = 0;
		int unauth = 0;
		int forbidden = 0;
		int not_found = 0;
		int service_unavail = 0;
		int others = 0;
		for (Integer code : urlmap.keySet()) {
			System.out.println(code);
			if (code / 100 == 2) {
				succeeded += urlmap.get(code);
			} else if (code / 100 == 3) {
				moved += urlmap.get(code);
				failed += urlmap.get(code);
			} else if (code == 401) {
				unauth += urlmap.get(code);
				failed += urlmap.get(code);
			} else if (code == 403) {
				forbidden += urlmap.get(code);
				failed += urlmap.get(code);
			} else if (code == 404) {
				not_found += urlmap.get(code);
				failed += urlmap.get(code);
			} else if (code == 503) {
				service_unavail += urlmap.get(code);
				failed += urlmap.get(code);
			} else {
				failed += urlmap.get(code);
				others += urlmap.get(code);
				System.out.println(code + "doesnt belong to any group");
			}
		}

		String fetchAttempted = "# fetch attempted: " + MyCrawler.fPages.size();
		String fetchSucceeded = "# fetch succeded: " + succeeded;
		String fetchAborted = "# fetch aborted: " + others;
		String fetchRedirects = "# fetch redirects: " + moved;
		String fetchForbidden = "# fetch forbidden: " + forbidden;
		String fetchNotFound = "# fetch not found: " + not_found;
		String fetchUnavil = "# fetch Unavailable: " + service_unavail;
		String fetchUnauth = "# fetch Unauthorized: " + unauth;
		String fetchFailed = "# fetch failed overall: " + failed;

		Set<WebPage> set = new HashSet<>(MyCrawler.allPages);
		int withIn = 0;
		for (WebPage wp : set) {
			if (wp.withinDomian == true) {
				withIn++;
			}
		}

		TreeMap<Integer, Integer> sizeMap = new TreeMap<>();
		for (ProcessedPage pp : MyCrawler.pPages) {
			if (pp.size < 1024) {
				if (sizeMap.get(1) != null) {
					sizeMap.put(1, sizeMap.get(1) + 1);
				} else {
					sizeMap.put(1, 1);
				}
			} else if (pp.size < 10240) {
				if (sizeMap.get(10) != null) {
					sizeMap.put(10, sizeMap.get(10) + 1);
				} else {
					sizeMap.put(10, 1);
				}
			} else if (pp.size < 102400) {
				if (sizeMap.get(100) != null) {
					sizeMap.put(100, sizeMap.get(100) + 1);
				} else {
					sizeMap.put(100, 1);
				}
			} else if (pp.size < 1024 * 1024) {
				if (sizeMap.get(1000) != null) {
					sizeMap.put(1000, sizeMap.get(1000) + 1);
				} else {
					sizeMap.put(1000, 1);
				}
			} else {
				if (sizeMap.get(1001) != null) {
					sizeMap.put(1001, sizeMap.get(1001) + 1);
				} else {
					sizeMap.put(1001, 1);
				}
			}
		}

		Hashtable<String, Integer> typeMap = new Hashtable<>();
		for (ProcessedPage pp : MyCrawler.pPages) {
			if (typeMap.get(pp.type) != null) {
				typeMap.put(pp.type, typeMap.get(pp.type) + 1);
			} else {
				typeMap.put(pp.type, 1);
			}
		}

		String totalUrl = "# URLs extracted: " + MyCrawler.allPages.size();
		String uniqueUrl = "# Unique URL : " + set.size();
		String uniqueUrlWithIn = "# Unique URL with-in: " + withIn;
		String uniqueUrlOutside = "# Unique URL outside: " + (set.size() - withIn);

		System.out.println("----------------------");
		System.out.println(fetchAttempted);
		System.out.println(fetchSucceeded);
		System.out.println(fetchRedirects);
		System.out.println(fetchForbidden);
		System.out.println(fetchNotFound);
		System.out.println(fetchUnavil);
		System.out.println(fetchUnauth);
		System.out.println(fetchAborted);
		System.out.println(fetchFailed);
		System.out.println("----------------------");
		System.out.println(totalUrl);
		System.out.println(uniqueUrl);
		System.out.println(uniqueUrlWithIn);
		System.out.println(uniqueUrlOutside);
		System.out.println("----------------------");
		for (Integer wp : urlmap.keySet()) {
			System.out.println(wp + " : " + urlmap.get(wp));
		}
		System.out.println("----------------------");
		for (Integer size : sizeMap.keySet()) {
			System.out.println(size + " : " + sizeMap.get(size));
		}
		System.out.println("----------------------");
		for (String type : typeMap.keySet()) {
			System.out.println(type + " : " + typeMap.get(type));
		}
		System.out.println("----------------------");
		Set<WebPage> wpSet = new HashSet<>(MyCrawler.allPages);
		System.out.println("# total URLs found" + MyCrawler.allPages.size());
		System.out.println("# unique URLs found" + wpSet.size());
		System.out.println("----------------------");
		System.out.println(new Date() + ", and started at " + start);
	}

}
