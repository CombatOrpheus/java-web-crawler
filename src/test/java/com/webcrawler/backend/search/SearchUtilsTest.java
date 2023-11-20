package com.webcrawler.backend.search;

import com.webcrawler.backend.search.DownloadProcess.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SearchUtilsTest {
	private static final String BASE = "http://hiring.axreng.com/";
	
	private static final String ANCHOR_ELEMENT = "<a class=\"literalurl\" href=\"manpageindex.html\" title=\"Manual page section index\">";

	@Test
	void shouldReturnTrueWhenContainsHref() {
		assertTrue(SearchUtils.containsHref(ANCHOR_ELEMENT));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"page.html", "./page.html", "./../../page.html"})
	void shouldReturnTrueForRelativeLinks(String input) {
		assertTrue(SearchUtils.validLinks(BASE, input));
	}
	
	@Test 
	void shouldReturnFalseWhenDoesNotContainHref() {
		assertFalse(SearchUtils.containsHref(BASE));
	}
	
	@Test
	void shouldReturnFalseOnAbsoluteLinksThatAreDifferent() {
		String testLink = "https://google.com.br";
		assertFalse(SearchUtils.validLinks(BASE, testLink));
	}

	@Test
	void shouldExtractHref() {
		assertFalse(SearchUtils.extractHref(ANCHOR_ELEMENT).isEmpty());
	}

	@ParameterizedTest
	@ValueSource(strings= {"page.html", "./page.html"})
	void shouldHandleLinksRelativeToCurrentPage(String input) {
		String expected = BASE + "page.html";
		String actual = SearchUtils.mapIntoAbsoluteLink(BASE, new Context(BASE, input));
		
		assertEquals(expected, actual);
	}
	
	@Test
	void shouldHandleRelativeLinksToSuperiorLevels() {
		String input = "../../../../page.html";
		String current = BASE + "1/2/3/4";
		String expected = BASE + "page.html";
		String actual = SearchUtils.mapIntoAbsoluteLink(BASE, new Context(current, input));
		
		assertEquals(expected, actual);
	}
	
}
