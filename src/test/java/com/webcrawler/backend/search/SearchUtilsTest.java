package com.webcrawler.backend.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.webcrawler.backend.search.DownloadProcess.Context;

class SearchUtilsTest {
	private static final String BASE = "http://hiring.axreng.com/";
	
	private static final String ANCHOR_ELEMENT = "<a class=\"literalurl\" href=\"manpageindex.html\" title=\"Manual page section index\">";

	@Test
	void testContainsHref() {
		assertTrue(SearchUtils.containsHref(ANCHOR_ELEMENT));
	}

	@Test
	void testExtractHref() {
		assertFalse(SearchUtils.extractHref(ANCHOR_ELEMENT).isEmpty());
	}

	@ParameterizedTest
	@ValueSource(strings= {"page.html", "./page.html"})
	void shouldHandleLinksRelativeToCurrentPage(String input) {
		String expected = BASE + "page.html";
		String actual = SearchUtils.handleLinks(new Context(BASE, input));
		
		assertEquals(expected, actual);
	}
	
	@Test
	void shouldHandleRelativeLinksToSuperiorLevels() {
		String input = "./../../../../page.html";
		String current = BASE + "1/2/3/4";
		String expected = BASE + "page.html";
		String actual = SearchUtils.handleLinks(new Context(current, input));
		
		assertEquals(expected, actual);
	}
	
}
