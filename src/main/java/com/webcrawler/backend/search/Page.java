package com.webcrawler.backend.search;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A class that hides the asynchronous download of an HTML page. It also offers
 * some simple methods for validating links and generating new absolute links to
 * new pages.
 */
final class Page {
	private final String url;
	private CompletableFuture<String> future;

	private final Pattern VALID_CHARACTERS = Pattern.compile("^\\w?\\d|/|\\./");
	private Predicate<String> startsWithValidCharacter = VALID_CHARACTERS.asPredicate();

	Page(String url) {
		this.url = url;
	}

	Page(String url, CompletableFuture<String> contents) {
		this.url = url;
		this.future = contents;
	}

	public String getUrl() {
		return this.url;
	}

	public void asyncDownload(CompletableFuture<String> future) {
		this.future = future;
	}

	/**
	 * @return The HTML contents of the page. This method might block if the
	 *         download has not completed when the method is called.
	 */
	public String getContents() {
		return this.future.join();
	}

	/**
	 * Maps a link to an absolute URL.
	 * @param link the link to map
	 * @return the absolute URL
	 */
	public String mapToAbsoluteLink(String link) {
		try {
			URI baseUri = new URI(this.url);
			URI resolvedUri = baseUri.resolve(link);
			return resolvedUri.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Checks if a link is valid.
	 * @param link the link to check
	 * @return true if the link is valid, false otherwise
	 */
	public boolean isValidLink(String link) {
		if (link == null || link.isBlank()) {
			return false;
		}
		if (link.startsWith("tel:") || link.startsWith("mailto:")) {
			return false;
		}
		try {
			URI uri = new URI(link);
			String scheme = uri.getScheme();
			return scheme == null || "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
		} catch (Exception e) {
			return false;
		}
	}
}
