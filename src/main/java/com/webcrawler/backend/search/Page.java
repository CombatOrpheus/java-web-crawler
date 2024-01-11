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
	private final String baseUrl;
	private final CompletableFuture<String> contents;

	private final Pattern LEVEL_ABOVE = Pattern.compile("^\\.\\./");
	private final Pattern VALID_CHARACTERS = Pattern.compile("^\\w?\\d|/|\\./");
	public Predicate<String> startsWithValidCharacter = VALID_CHARACTERS.asPredicate();
	private final Pattern RELATIVE_CURRENT = Pattern.compile("^(\\./)|/");

	Page(String url, CompletableFuture<String> contents) {
		this.url = url;
		this.contents = contents;

		int index = url.indexOf('/', 9);
		if (index > 0) {
			this.baseUrl = url.substring(0, index);
		} else {
			this.baseUrl = url;
		}
	}

	public String getUrl() {
		return this.url;
	}

	/**
	 * @return The HTML contents of the page. This method might block if the
	 *         download has not completed when the method is called.
	 */
	public String getContents() {
		return this.contents.join();
	}

	public String mapToAbsoluteLink(String link) {
		if (link.startsWith("http")) { // Absolute Links
			return link;
		} else if (link.startsWith("../")) { // Above current level
			String copy = url;
			String cleanedLink = link;
			for (int i = 0; i < levelsAbove(link); i++) {
				copy = copy.substring(0, link.lastIndexOf('/'));
				cleanedLink = cleanedLink.substring(3, cleanedLink.length());
			}
			return copy + "/" + cleanedLink;

		} else { // Relative to current level
			if (RELATIVE_CURRENT.asMatchPredicate().test(link)) {
				int start = RELATIVE_CURRENT.matcher(link).end();
				String base = url.substring(0, url.lastIndexOf('/') + 1);
				String complement = link.substring(start);
				return base + complement;
			} else {
				return url + "/" + link;
			}
		}
	}

	public boolean isValidLink(String link) {
		if (link.startsWith("http")) {
			return link.startsWith(baseUrl);
		} else if (link.startsWith("tel") || link.startsWith("mail")) {
			return false;
		} else {
			return startsWithValidCharacter.test(link);
		}
	}

	private int levelsAbove(String link) {
		int total = 0;
		LEVEL_ABOVE.matcher(link).results().count();
		return total;

	}
}
