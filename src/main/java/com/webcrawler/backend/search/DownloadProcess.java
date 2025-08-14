package com.webcrawler.backend.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import java.util.stream.Collectors;

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
	 * processing is done with a {@link java.util.stream.Stream}.
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

			getLinks(page).stream()
					.map(link -> new Page(link, downloader.downloadPage(link)))
					.forEach(searchQueue::add);

		} while (!searchQueue.isEmpty());
		logger.info("Download Process complete");
	}

	public List<String> getLinks(Page page) {
		Document doc = Jsoup.parse(page.getContents());
		return doc.select("a[href]").stream()
				.map(element -> element.attr("href"))
				.filter(Objects::nonNull)
				.peek(link -> logger.info("Found link: {}", link))
				.filter(page::isValidLink)
				.map(page::mapToAbsoluteLink)
				.filter(this::isSameSite)
				.distinct()
				.map(this::checkVisitedLinks)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
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

	private boolean isSameSite(String url) {
		try {
			URI uri = new URI(url);

			if (uri.getHost() == null) {
				return false;
			}

			return uri.getHost().equals(baseUri.getHost());
		} catch (URISyntaxException e) {
			logger.warn("Invalid URL found: {}", url);
			return false;
		}
	}
}
