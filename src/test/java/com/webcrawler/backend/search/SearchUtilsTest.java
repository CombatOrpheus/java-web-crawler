package com.webcrawler.backend.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.webcrawler.backend.search.DownloadProcess.Context;

class SearchUtilsTest {
	private static final String BASE = Constants.BASE_URL;
	
	private static final String ANCHOR_ELEMENT = "<a class=\"literalurl\" href=\"manpageindex.html\" title=\"Manual page section index\">";

	@Test
	void testContainsHref() {
		assertTrue(SearchUtils.containsHref(ANCHOR_ELEMENT));
	}

	@Test
	void testExtractHref() {
		assertFalse(SearchUtils.extractHref(ANCHOR_ELEMENT).isEmpty());
	}

	@Test
	void shouldHandleLinksRelativeToCurrentPage() {
		String input = "page.html";
		String expected = BASE + input;
		String actual = SearchUtils.handleLinks(new Context(BASE, input));
		
		assertEquals(expected, actual);
	}
	
	@Test
	void shouldHandleArbitraryRelativeLinks() {
		String input = "./page.html";
		String expected = BASE + "page.html";
		String actual = SearchUtils.handleLinks(new Context(BASE, input));
		
		assertEquals(expected, actual);
	}
	
}
