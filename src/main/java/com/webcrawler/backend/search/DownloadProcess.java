package com.webcrawler.backend.search;

import static com.webcrawler.backend.constants.Constants.BASE_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main download process, which asynchronously and recursively searches for
 * all the valid links in the HTML page. The process ends when all of the
 * possible links are visited. The main entry point is the {@link this#crawl()}
 * function.
 */
public final class DownloadProcess {
	// TODO Implement logging

	private static Logger logger = LoggerFactory.getLogger(DownloadProcess.class);

	/**
	 * A {@link Queue} containing the links to new valid pages.
	 */
	private static final Queue<Page> SEARCH_QUEUE = new ConcurrentLinkedQueue<Page>();
	/**
	 * Pages that have been downloaded are kept in memory and made available for the
	 * {@link SearchProcess} so that they are only downloaded once.
	 */
	private static final List<Page> DOWNLOADED_PAGES = new ArrayList<>();
	/**
	 * Every visited page is kept in this {@link Set} so as to avoid multiple visits
	 * to the same URL.
	 */
	private static final Set<String> VISITED_PAGES = new TreeSet<>();

	/**
	 * A {@link Pattern} for the detection of anchor elements on the HTML pages.
	 */
	private static final Pattern ANCHOR = Pattern.compile("<a(.*[^>])>");

	/**
	 * A simple record that holds the link to the current HTML page and a new link
	 * that is either the absolute link to a new page or a relative link.
	 */
	record Context(String url, String newUrl) {
	}

	/**
	 * The main method for the search, validation and download of HTML pages. It
	 * remains active s long as there are elements in an internal search queue. The
	 * processing is done with a {@link Stream}, which has additional comments for
	 * clarity.
	 */
	public static void crawl() {
		SEARCH_QUEUE.add(new Page(BASE_URL));
		do {
			Page page = SEARCH_QUEUE.poll();
			logger.info("Searching for new links in page " + page.url());

			DOWNLOADED_PAGES.add(page);
			String contents = page.contents();

			ANCHOR.matcher(contents) // Search for the HTML anchor elements in a page
					.results() // Get a Stream with the regex matches
					.map(MatchResult::group) // Get the matching Strings
					.filter(SearchUtils::containsHref) // Remove the elements that do not contain the field href
					.map(SearchUtils::extractHref) // Extract the link contained in the href
					.filter(SearchUtils::validLinks) // Remove links that lead to other pages
					.map(link -> new Context(page.url(), link)) // Create a pair with the current page and the new link
					.map(SearchUtils::mapIntoAbsoluteLink) // Generate the absolute links from the relative links
					.map(DownloadProcess::checkVisitedLinks)
					.filter(link -> !link.isBlank())
					.map(Page::new) // Create a new Page object and start the download process
					.forEach(SEARCH_QUEUE::add); // Add these new pages for the next step of the search

		} while (!SEARCH_QUEUE.isEmpty());
		logger.info("Download Process complete!");
	}

	public static boolean isComplete() {
		return SEARCH_QUEUE.isEmpty();
	}

	public static List<Page> getDownloadedPages() {
		return DOWNLOADED_PAGES;
	}

	public static Set<String> getVisitedPages() {
		return VISITED_PAGES;
	}

	private static String checkVisitedLinks(String string) {
		if (VISITED_PAGES.contains(string)) {
			return "";
		}
		VISITED_PAGES.add(string);
		return string;
	}
}
