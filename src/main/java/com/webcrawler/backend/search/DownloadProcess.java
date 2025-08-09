package com.webcrawler.backend.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * The main download process, which asynchronously and recursively searches for
 * all the valid links in the HTML page. The process ends when all of the
 * possible links are visited. The main entry point is the {@link this#run()}
 * function.
 */
public final class DownloadProcess implements DownloadProcessInterface {
	private final String baseUrl;
	private final URI baseUri;

	private static final Logger logger = LoggerFactory.getLogger(DownloadProcess.class);

	private final HttpDownloader downloader = new HttpDownloader();

	/**
	 * A {@link Queue} containing the links to new valid pages.
	 */
	private final Queue<Page> searchQueue = new ConcurrentLinkedQueue<>();
	/**
	 * Pages that have been downloaded are kept in memory and made available for the
	 * {@link SearchProcess} so that they are only downloaded once.
	 */
	private final List<Page> downloadedPages = new ArrayList<>();
	/**
	 * Every visited page is kept in this {@link Set} to avoid multiple visits to
	 * the same URL.
	 */
	private final Set<String> visitedPages = new TreeSet<>();

	/**
	 * A {@link Pattern} for the detection of anchor elements on the HTML pages.
	 */
	private static final Pattern ANCHOR = Pattern.compile("<a(.*?)>");
	private static final Pattern HREF = Pattern.compile("href=\"(.*?)\"");

	public DownloadProcess(String baseUrl) {
		this.baseUrl = baseUrl;
		try {
			this.baseUri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid baseUrl: " + baseUrl, e);
		}
	}

	/**
	 * The main method for the search, validation and download of HTML pages. It
	 * remains active s long as there are elements in an internal search queue. The
	 * processing is done with a {@link Stream}.
	 */
	@Override
	public void run() {
		searchQueue.add(new Page(baseUrl, downloader.downloadPage(baseUrl)));
		do {
			Page page = searchQueue.poll();
			if (page == null) {
				continue;
			}
			logger.info("Searching for new links in page " + page.getUrl());

			downloadedPages.add(page);
			String contents = page.getContents();

			ANCHOR.matcher(contents) // Search for the HTML anchor elements in a page
					.results() // Get a Stream with the regex matches
					.map(MatchResult::group) // Get the matching Strings
					.map(this::extractHref)
					.filter(Objects::nonNull)
					.peek(link -> logger.info("Found link: {}", link))
					.filter(page::isValidLink)
					.map(page::mapToAbsoluteLink)
					.filter(this::isSameSite)
					.map(this::checkVisitedLinks)
					.filter(Objects::nonNull)
					.map(link -> new Page(link, downloader.downloadPage(link)))
					.forEach(searchQueue::add);

		} while (!searchQueue.isEmpty());
		logger.info("Download Process complete");
	}

	public boolean isComplete() {
		return searchQueue.isEmpty();
	}

	public List<Page> getDownloadedPages() {
		return downloadedPages;
	}

	public Set<String> getVisitedPages() {
		return visitedPages;
	}

	/**
	 * If the link has already been visited, make it a null, otherwise, add it to
	 * the list and return it.
	 */
	private String checkVisitedLinks(String string) {
		if (visitedPages.contains(string)) {
			return null;
		}
		visitedPages.add(string);
		return string;
	}

	/**
	 * Extract the href from the anchor element. A proper HTML parser
	 * would be more robust.
	 */
	private String extractHref(String element) {
		var matcher = HREF.matcher(element);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private boolean isSameSite(String url) {
		try {
			URI uri = new URI(url);
			logger.info("Checking site: url={}, baseUri={}, uri={}, baseUri.host={}, uri.host={}, baseUri.port={}, uri.port={}", url, baseUri, uri, baseUri.getHost(), uri.getHost(), baseUri.getPort(), uri.getPort());
			return baseUri.getHost().equals(uri.getHost()) && baseUri.getPort() == uri.getPort();
		} catch (URISyntaxException e) {
			logger.warn("Invalid URL found: {}", url);
			return false;
		}
	}
}
