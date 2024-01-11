package com.webcrawler.backend.search;

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

	public String mapToAbsoluteLink(String link) {
		if (link.startsWith("http")) { // Absolute Links
			return link;
		} else if (link.startsWith("../")) { // Above current level
			String copy = url;
			String cleanedLink = link;
			while (cleanedLink.startsWith("../")) {
				copy = copy.substring(0, copy.lastIndexOf('/'));
				cleanedLink = cleanedLink.substring(3, cleanedLink.length());
			}
			return copy + "/" + cleanedLink;

		} else { // Relative to current level
			if (link.startsWith("/") || link.startsWith("./")) {
				int start = link.indexOf('/');
				String base = url.substring(0, url.lastIndexOf('/'));
				String complement = link.substring(start);
				return base + complement;
			} else {
				return url + link;
			}
		}
	}

	public boolean isValidLink(String link) {
		if (link.startsWith("http")) {
			return true;
		} else if (link.startsWith("tel") || link.startsWith("mail")) {
			return false;
		} else {
			return startsWithValidCharacter.test(link);
		}
	}
}
