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
	void shouldReturnTrueWhenContainsHref() {
		assertTrue(SearchUtils.containsHref(ANCHOR_ELEMENT));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"page.html", "./page.html", "./../../page.html"})
	void shouldReturnTrueForRelativeLinks(String input) {
		assertTrue(SearchUtils.validLinks(input));
	}
	
	@Test 
	void shouldReturnFalseWhenDoesNotContainHref() {
		assertFalse(SearchUtils.containsHref(BASE));
	}
	
	@Test
	void shouldReturnFalseOnAbsoluteLinksThatAreDifferent() {
		String testLink = "https://google.com.br";
		assertFalse(SearchUtils.validLinks(testLink));
	}

	@Test
	void shouldExtractHref() {
		assertFalse(SearchUtils.extractHref(ANCHOR_ELEMENT).isEmpty());
	}

	@ParameterizedTest
	@ValueSource(strings= {"page.html", "./page.html"})
	void shouldHandleLinksRelativeToCurrentPage(String input) {
		String expected = BASE + "page.html";
		String actual = SearchUtils.mapIntoAbsoluteLink(new Context(BASE, input));
		
		assertEquals(expected, actual);
	}
	
	@Test
	void shouldHandleRelativeLinksToSuperiorLevels() {
		String input = "./../../../../page.html";
		String current = BASE + "1/2/3/4";
		String expected = BASE + "page.html";
		String actual = SearchUtils.mapIntoAbsoluteLink(new Context(current, input));
		
		assertEquals(expected, actual);
	}
	
}
