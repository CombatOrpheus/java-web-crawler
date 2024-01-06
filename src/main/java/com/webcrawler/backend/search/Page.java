package com.webcrawler.backend.search;

import java.util.concurrent.CompletableFuture;

/**
 * A class that hides the asynchronous download of an HTML page. It also offers
 * some simple methods for validating links and generating new absolute links to
 * new pages.
 */
final class Page {
	private final String url;
	private final CompletableFuture<String> contents;

	Page(String url) {
		this.url = url;
		this.contents = HttpDownloader.downloadPage(url);
	}

	String url() {
		return this.url;
	}

	/**
	 * @return The HTML contents of the page. This might block if the download has
	 *         not completed when the method is called.
	 */
	String contents() {
		return this.contents.join().toLowerCase();
	}

	boolean isValidLink(String link) {
		return true;
	}

	String mapToAbsoluteLink(String link) {
		return null;
	}

}
