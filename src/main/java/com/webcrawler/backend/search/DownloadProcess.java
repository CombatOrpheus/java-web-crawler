package com.webcrawler.backend.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class DownloadProcess {

	private static final String BASE_URL = System.getenv("BASE_URL");

	private static final Queue<Page> DOWNLOAD_QUEUE = new ConcurrentLinkedQueue<Page>();
	private static final List<Page> DOWNLOADED_PAGES = new ArrayList<>();
	private static final Set<String> VISITED_PAGES = new HashSet<>();
	
	private static final Pattern ANCHOR = Pattern.compile("<a.+?>");

	record Context(String url, String newUrl) {}
	
	public static void crawl() {
		DOWNLOAD_QUEUE.add(new Page(BASE_URL));
		do {
			Page page = DOWNLOAD_QUEUE.poll();
			DOWNLOAD_QUEUE.add(page);
			String contents = page.contents();
			ANCHOR.matcher(contents)                               // Search for the HTML elements in a page
					.results()                                     // Get a Stream with the regex matches
					.map(MatchResult::group)                       // Get the matching Strings
					.filter(SearchUtils::containsHref)             // Remove the elements that do not contain the field href
					.map(SearchUtils::extractHref)                 // Extract the link contained in the href
					.filter(SearchUtils::validLinks)               // Validate the links
					.filter(String::isEmpty)
					.map(link -> new Context(page.url(), link))    // Create a pair with the current page and the new link
					.map(SearchUtils::handleLinks)                 // Handle relative links
					.filter(link -> !VISITED_PAGES.contains(link)) // Remove the links that we have already visited
					.map(Page::new)                                // Create a new object and start the download process
					.forEach(DOWNLOAD_QUEUE::add);
			
		} while (!DOWNLOAD_QUEUE.isEmpty());
	}

	public static boolean isComplete() {
		return DOWNLOAD_QUEUE.isEmpty();
	}
	
	public static List<Page> getDownloadedPages() {
		return DOWNLOADED_PAGES;
	}
	
	public static Set<String> getVisitedPages() {
		return VISITED_PAGES;
	}
}
