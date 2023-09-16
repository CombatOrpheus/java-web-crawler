package com.webcrawler.backend.search;

import static com.webcrawler.backend.constants.Constants.BASE_URL;

import com.webcrawler.backend.search.DownloadProcess.Context;

/**
 * Several utility functions, extracted into their own class in order to ease
 * testing.
 */
public final class SearchUtils {

	private static final String HREF = "href=\"";

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
	static String handleLinks(Context context) {
		String currentPage = context.url();
		String newPage = context.newUrl();

		if (newPage.startsWith(BASE_URL)) { // Is it an absolute link?
			return newPage;
		} else {
			// TODO Add some useful comments
			String[] parts = newPage.split("/");
			
			// Simple links on the form "./page" or just "page"
			if (parts.length < 3) {
				return currentPage + parts[parts.length-1];
			}
			
			String relativeLevel = currentPage;
			StringBuilder relativeLink = new StringBuilder();
			for (String part: parts) {
				if (part.equals("..")) {
					int end = relativeLevel.lastIndexOf("/");
					relativeLevel = currentPage.substring(0, end);
				}
				
				if (!part.startsWith(".")) {
					relativeLink.append("/");
					relativeLink.append(part);
				}
			}
			return relativeLevel + relativeLink.toString();
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
