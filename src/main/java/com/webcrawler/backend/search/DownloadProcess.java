package com.webcrawler.backend.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
 * possible links are visited. The main entry point is the {@link this#run()}
 * function.
 */
public final class DownloadProcess implements Runnable {
	private final String baseUrl;

	private static final String HREF = "href=\"";

	private static final Logger logger = LoggerFactory.getLogger(DownloadProcess.class);

	private final HttpDownloader downloader = new HttpDownloader();

	/**
	 * A {@link Queue} containing the links to new valid pages.
	 */
	private static final Queue<Page> SEARCH_QUEUE = new ConcurrentLinkedQueue<>();
	/**
	 * Pages that have been downloaded are kept in memory and made available for the
	 * {@link SearchProcess} so that they are only downloaded once.
	 */
	private static final List<Page> DOWNLOADED_PAGES = new ArrayList<>();
	/**
	 * Every visited page is kept in this {@link Set} to avoid multiple visits to
	 * the same URL.
	 */
	private static final Set<String> VISITED_PAGES = new TreeSet<>();

	/**
	 * A {@link Pattern} for the detection of anchor elements on the HTML pages.
	 */
	private static final Pattern ANCHOR = Pattern.compile("<a(.*[^>])>");

	public DownloadProcess(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * The main method for the search, validation and download of HTML pages. It
	 * remains active s long as there are elements in an internal search queue. The
	 * processing is done with a {@link Stream}.
	 */
	@Override
	public void run() {
		SEARCH_QUEUE.add(new Page(baseUrl, downloader.downloadPage(baseUrl)));
		do {
			Page page = SEARCH_QUEUE.poll();
			logger.info("Searching for new links in page " + page.getUrl());

			DOWNLOADED_PAGES.add(page);
			String contents = page.getContents();

			ANCHOR.matcher(contents) // Search for the HTML anchor elements in a page
					.results() // Get a Stream with the regex matches
					.map(MatchResult::group) // Get the matching Strings
					.filter(this::containsHref).map(this::extractHref).filter(page.startsWithValidCharacter)
					.map(page::mapToAbsoluteLink).map(DownloadProcess::checkVisitedLinks).filter(Objects::nonNull)
					.map(link -> new Page(link, downloader.downloadPage(link))).forEach(SEARCH_QUEUE::add);

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

	/**
	 * If the link has already been visited, make it a null, otherwise, add it to
	 * the list and return it.
	 */
	private static String checkVisitedLinks(String string) {
		if (VISITED_PAGES.contains(string)) {
			return null;
		}
		VISITED_PAGES.add(string);
		return string;
	}

	private boolean containsHref(String string) {
		return string.contains(HREF);
	}

	/**
	 * Extract the href from the anchor element. This method assumes that the href
	 * is the first quoted element to appear on anchor element.
	 */
	private String extractHref(String element) {
		int start = element.indexOf(HREF) + HREF.length();
		int end = element.indexOf("\"", start);
		return element.substring(start, end);
	}
}
