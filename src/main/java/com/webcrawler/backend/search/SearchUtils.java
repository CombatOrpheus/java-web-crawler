package com.webcrawler.backend.search;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.webcrawler.backend.search.DownloadProcess.Context;

/**
 * Several utility functions, extracted into their own class in order to ease
 * testing.
 */
public final class SearchUtils {

	private static final String BASE_URL = System.getenv("BASE_URL");

	private static final Pattern HREF = Pattern.compile("href=\"(.+)\"");
	private static final Pattern RELATIVE = Pattern.compile("../");
	private static final Predicate<String> CONTAINS_HREF = HREF.asPredicate();

	static boolean containsHref(String string) {
		return CONTAINS_HREF.test(string);
	}

	static String extractHref(String element) {
		return HREF.matcher(element).group(1);
	}

	/**
	 * Distinguishes between absolute and relative links to the current page being
	 * searched.
	 * 
	 * @param context A {@link Context} with the current page and the possibly new
	 *                page
	 * @return The correctly formatted new link.
	 */
	static String handleLinks(Context context) {
		String currentPage = context.url();
		String newPage = context.newUrl();

		if (newPage.startsWith(BASE_URL)) { // Is it an absolute link?
			return newPage;
		} else {
			// TODO Implement the handling of relative links
			StringBuilder newLink = new StringBuilder(currentPage);
			return newLink.toString();
		}
	}

	/**
	 * @param link The link to be checked
	 * @return {@value true} if the link is absolute or relative to the
	 *         {@systemProperty BASE_URL}, otherwise, {@value false}.
	 */
	static boolean validLinks(String link) {
		return link.startsWith(BASE_URL) || !link.startsWith("http") ;
	}
}
