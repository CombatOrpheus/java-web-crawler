package com.webcrawler.backend.search;

import static com.webcrawler.backend.constants.Constants.BASE_URL;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.webcrawler.backend.search.DownloadProcess.Context;

/**
 * Several utility functions, extracted into their own class in order to ease
 * testing.
 */
public final class SearchUtils {

	private static final String HREF = "href=\"";
	private static final Pattern LEVELS_ABOVE = Pattern.compile("\\.\\./");
	private static final Predicate<String> HAS_LEVELS_ABOVE = LEVELS_ABOVE.asMatchPredicate();

	static boolean containsHref(String string) {
		return string.contains(HREF);
	}

	static String extractHref(String element) {
		int start = element.indexOf(HREF) + HREF.length();
		int end = element.indexOf("\"", start);
		return element.substring(start, end);
	}

	/**
	 * Generates the link to a new page, given the current page and new link.
	 * 
	 * @param context A {@link Context} with the current page and the new link.
	 * @return The correctly formatted link to a new page.
	 */
	static String mapIntoAbsoluteLink(Context context) {
		String currentPage = context.url();
		String newPage = context.newUrl();

		if (newPage.startsWith(BASE_URL)) { // Is it an absolute link?
			return newPage;
		} else { //Relative links
			return handleRelativeLinks(currentPage, newPage);
		}
	}

	private static String handleRelativeLinks(String base, String relative) {
		if (HAS_LEVELS_ABOVE.test(relative)) { // Relative to levels above the current one
			int levelsToRemove = (int) LEVELS_ABOVE.matcher(relative).results().count();
			for (int i = 0; i < levelsToRemove; i++) {
				int lastFowardSlash = base.lastIndexOf("/");
				base = base.substring(0, lastFowardSlash);
			}
			
			return base + relative.replaceAll("../", "");
		} else { // relative to the current level
			int lastFowardSlash = base.lastIndexOf("/") + 1;
			return base.substring(0, lastFowardSlash) + relative.replace("./", ""); 
		}
	}

	/**
	 * @param link The link to be checked
	 * @return <b>true</b> if the link is absolute or relative to the
	 *         {@} otherwise, <b>false</b>.
	 */
	static boolean validLinks(String link) {
		return link.startsWith(BASE_URL) || !link.startsWith("http");
	}
}
